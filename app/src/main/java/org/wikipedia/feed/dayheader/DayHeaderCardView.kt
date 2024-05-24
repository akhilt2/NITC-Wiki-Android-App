package org.akhil.nitcwiki.feed.dayheader

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.akhil.nitcwiki.databinding.ViewCardDayHeaderBinding
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.feed.view.FeedCardView

class DayHeaderCardView constructor(context: Context) : FrameLayout(context), FeedCardView<Card> {

    private val binding = ViewCardDayHeaderBinding.inflate(LayoutInflater.from(context), this, true)

    override var callback: FeedAdapter.Callback? = null

    override var card: Card? = null
        set(value) {
            field = value
            value?.let {
                binding.dayHeaderText.text = it.title()
            }
        }
}
