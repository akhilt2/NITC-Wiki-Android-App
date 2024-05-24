package org.akhil.nitcwiki.feed.random

import android.content.Context
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.database.AppDatabase
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.FeedContentType
import org.akhil.nitcwiki.feed.FeedCoordinator
import org.akhil.nitcwiki.feed.dataclient.FeedClient
import org.akhil.nitcwiki.readinglist.database.ReadingListPage
import org.akhil.nitcwiki.util.log.L

class RandomClient : FeedClient {

    private var clientJob: Job? = null

    override fun request(context: Context, wiki: WikiSite, age: Int, cb: FeedClient.Callback) {
        cancel()
        clientJob = CoroutineScope(Dispatchers.Main).launch(
            CoroutineExceptionHandler { _, caught ->
                L.v(caught)
                cb.error(caught)
            }
        ) {
            val list = mutableListOf<RandomCard>()
            FeedContentType.aggregatedLanguages.forEach { lang ->
                val wikiSite = WikiSite.forLanguageCode(lang)
                val randomSummary = try {
                    ServiceFactory.getRest(wikiSite).getRandomSummary()
                } catch (e: Exception) {
                    AppDatabase.instance.readingListPageDao().getRandomPage()?.let {
                        ReadingListPage.toPageSummary(it)
                    } ?: run {
                        throw e
                    }
                }
                list.add(RandomCard(randomSummary, age, wikiSite))
            }
            FeedCoordinator.postCardsToCallback(cb, list)
        }
    }

    override fun cancel() {
        clientJob?.cancel()
    }
}
