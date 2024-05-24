package org.akhil.nitcwiki.feed.view

import org.akhil.nitcwiki.feed.model.Card

interface FeedCardView<T : Card?> {
    var card: T?
    var callback: FeedAdapter.Callback?
}
