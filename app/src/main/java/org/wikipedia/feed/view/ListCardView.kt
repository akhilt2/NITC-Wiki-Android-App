package org.akhil.nitcwiki.feed.view

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewListCardBinding
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.views.DrawableItemDecoration

abstract class ListCardView<T : Card?>(context: Context) : DefaultFeedCardView<T>(context) {
    interface Callback {
        fun onFooterClick(card: Card)
    }

    private val binding = ViewListCardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.viewListCardList.layoutManager = LinearLayoutManager(context)
        binding.viewListCardList.addItemDecoration(
            DrawableItemDecoration(context, R.attr.list_divider, drawStart = false, drawEnd = false)
        )
        binding.viewListCardList.isNestedScrollingEnabled = false
    }

    override var callback: FeedAdapter.Callback? = null
        set(value) {
            field = value
            binding.viewListCardHeader.setCallback(value)
        }

    protected fun set(adapter: RecyclerView.Adapter<*>?) {
        binding.viewListCardList.adapter = adapter
    }

    protected fun update() {
        binding.viewListCardList.adapter?.notifyDataSetChanged()
    }

    protected val headerView get() = binding.viewListCardHeader

    protected val footerView get() = binding.viewListCardFooter

    protected val largeHeaderContainer get() = binding.viewListCardLargeHeaderContainer

    protected val largeHeaderView get() = binding.viewListCardLargeHeader

    protected val layoutDirectionView get() = binding.viewListCardList
}
