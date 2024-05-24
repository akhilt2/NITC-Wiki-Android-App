package org.akhil.nitcwiki.feed.dataclient

import android.content.Context
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.FeedCoordinator
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.searchbar.SearchCard

abstract class DummyClient : FeedClient {

    override fun request(context: Context, wiki: WikiSite, age: Int, cb: FeedClient.Callback) {
        getNewCard(wiki).let {
            if (it is SearchCard) {
                cb.success(listOf(it))
            } else {
                FeedCoordinator.postCardsToCallback(cb, listOf(it))
            }
        }
    }

    override fun cancel() {}
    abstract fun getNewCard(wiki: WikiSite?): Card
}
