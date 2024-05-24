package org.akhil.nitcwiki.feed.mainpage

import android.content.Context
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.FeedContentType
import org.akhil.nitcwiki.feed.FeedCoordinator
import org.akhil.nitcwiki.feed.dataclient.FeedClient

class MainPageClient : FeedClient {

    override fun request(context: Context, wiki: WikiSite, age: Int, cb: FeedClient.Callback) {
        val cards = WikipediaApp.instance.languageState.appLanguageCodes
            .filterNot { FeedContentType.MAIN_PAGE.langCodesDisabled.contains(it) }
            .map { MainPageCard(WikiSite.forLanguageCode(it)) }
        FeedCoordinator.postCardsToCallback(cb, cards)
    }

    override fun cancel() {}
}
