package org.akhil.nitcwiki.feed.dataclient

import android.content.Context
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.model.Card

interface FeedClient {

    interface Callback {
        fun success(cards: List<Card>)
        fun error(caught: Throwable)
    }

    fun request(context: Context, wiki: WikiSite, age: Int, cb: Callback)
    fun cancel()
}
