package org.akhil.nitcwiki.feed

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.feed.aggregated.AggregatedFeedContentClient
import org.akhil.nitcwiki.feed.announcement.AnnouncementClient
import org.akhil.nitcwiki.feed.dataclient.FeedClient
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.offline.OfflineCardClient
import org.akhil.nitcwiki.feed.onboarding.OnboardingClient
import org.akhil.nitcwiki.feed.searchbar.SearchClient

class FeedCoordinator internal constructor(context: Context) : FeedCoordinatorBase(context) {

    private val aggregatedClient = AggregatedFeedContentClient()

    init {
        FeedContentType.restoreState()
    }

    override fun reset() {
        super.reset()
        aggregatedClient.invalidate()
    }

    override fun buildScript(age: Int) {
        val online = WikipediaApp.instance.isOnline
        conditionallyAddPendingClient(SearchClient(), age == 0)
        conditionallyAddPendingClient(AnnouncementClient(), age == 0 && online)
        conditionallyAddPendingClient(OnboardingClient(), age == 0)
        conditionallyAddPendingClient(OfflineCardClient(), age == 0 && !online)

        for (contentType in FeedContentType.entries.sortedBy { it.order }) {
            addPendingClient(contentType.newClient(aggregatedClient, age))
        }
    }

    companion object {
        fun postCardsToCallback(cb: FeedClient.Callback, cards: List<Card>) {
            CoroutineScope(Dispatchers.Default).launch {
                delay(150L)
                withContext(Dispatchers.Main) {
                    cb.success(cards)
                }
            }
        }
    }
}
