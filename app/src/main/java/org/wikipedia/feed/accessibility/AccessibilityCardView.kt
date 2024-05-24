package org.akhil.nitcwiki.feed.accessibility

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.akhil.nitcwiki.databinding.ViewCardAccessibilityBinding
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.feed.view.FeedCardView

class AccessibilityCardView(context: Context) : LinearLayout(context), FeedCardView<Card> {

    private val binding = ViewCardAccessibilityBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.loadMore.setOnClickListener { onLoadMoreClick() }
    }

    override var card: Card? = null

    override var callback: FeedAdapter.Callback? = null

    private fun onLoadMoreClick() {
        callback?.onRequestMore()
    }
}
