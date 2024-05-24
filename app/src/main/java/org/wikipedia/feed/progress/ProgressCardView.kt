package org.akhil.nitcwiki.feed.progress

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.akhil.nitcwiki.databinding.ViewCardProgressBinding
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.feed.view.FeedCardView

class ProgressCardView(context: Context) : FrameLayout(context), FeedCardView<Card> {

    init {
        ViewCardProgressBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override var callback: FeedAdapter.Callback? = null
    override var card: Card? = null
}
