package org.akhil.nitcwiki.feed.view

import android.content.Context
import android.util.AttributeSet
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.akhil.nitcwiki.databinding.ViewListCardItemBinding
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.PageAvailableOfflineHandler
import org.akhil.nitcwiki.readinglist.LongPressMenu
import org.akhil.nitcwiki.readinglist.database.ReadingListPage
import org.akhil.nitcwiki.util.DeviceUtil
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.TransitionUtil
import org.akhil.nitcwiki.views.ViewUtil

class ListCardItemView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    interface Callback {
        fun onSelectPage(card: Card, entry: HistoryEntry, openInNewBackgroundTab: Boolean)
        fun onSelectPage(card: Card, entry: HistoryEntry, sharedElements: Array<Pair<View, String>>)
        fun onAddPageToList(entry: HistoryEntry, addToDefault: Boolean)
        fun onMovePageToList(sourceReadingListId: Long, entry: HistoryEntry)
    }

    private val binding = ViewListCardItemBinding.inflate(LayoutInflater.from(context), this)
    private var card: Card? = null

    @get:VisibleForTesting
    var callback: Callback? = null
        private set

    @get:VisibleForTesting
    var historyEntry: HistoryEntry? = null
        private set

    init {
        isFocusable = true
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val topBottomPadding = 16
        setPadding(0, DimenUtil.roundedDpToPx(topBottomPadding.toFloat()),
            0, DimenUtil.roundedDpToPx(topBottomPadding.toFloat()))
        DeviceUtil.setContextClickAsLongClick(this)
        setBackgroundResource(ResourceUtil.getThemedAttributeId(context, androidx.appcompat.R.attr.selectableItemBackground))

        setOnClickListener {
            if (historyEntry != null && card != null) {
                callback?.onSelectPage(card!!, historyEntry!!, TransitionUtil.getSharedElements(context, binding.viewListCardItemImage))
            }
        }

        setOnLongClickListener { view ->
            LongPressMenu(view, callback = object : LongPressMenu.Callback {
                override fun onOpenLink(entry: HistoryEntry) {
                    card?.let {
                        callback?.onSelectPage(it, entry, false)
                    }
                }

                override fun onOpenInNewTab(entry: HistoryEntry) {
                    card?.let {
                        callback?.onSelectPage(it, entry, true)
                    }
                }

                override fun onAddRequest(entry: HistoryEntry, addToDefault: Boolean) {
                    callback?.onAddPageToList(entry, addToDefault)
                }

                override fun onMoveRequest(page: ReadingListPage?, entry: HistoryEntry) {
                    page?.let {
                        callback?.onMovePageToList(it.listId, entry)
                    }
                }
            }).show(historyEntry)
            false
        }
    }

    fun setCard(card: Card?): ListCardItemView {
        this.card = card
        return this
    }

    fun setCallback(callback: Callback?): ListCardItemView {
        this.callback = callback
        return this
    }

    fun setHistoryEntry(entry: HistoryEntry): ListCardItemView {
        historyEntry = entry
        setTitle(StringUtil.fromHtml(entry.title.displayText))
        setSubtitle(entry.title.description)
        setImage(entry.title.thumbUrl)
        val lifecycleScope = (context as? AppCompatActivity)?.lifecycleScope ?: CoroutineScope(Dispatchers.IO)
        PageAvailableOfflineHandler.check(lifecycleScope, entry.title) { setViewsGreyedOut(!it) }
        return this
    }

    @VisibleForTesting
    fun setImage(url: String?) {
        if (url == null) {
            binding.viewListCardItemImage.visibility = GONE
        } else {
            binding.viewListCardItemImage.visibility = VISIBLE
            ViewUtil.loadImageWithRoundedCorners(binding.viewListCardItemImage, url, true)
        }
    }

    @VisibleForTesting
    fun setTitle(text: CharSequence?) {
        binding.viewListCardItemTitle.text = text
    }

    @VisibleForTesting
    fun setSubtitle(text: CharSequence?) {
        binding.viewListCardItemSubtitle.text = text
    }

    fun setNumber(number: Int) {
        binding.viewListCardNumber.visibility = VISIBLE
        binding.viewListCardNumber.setNumber(number)
    }

    fun setPageViews(pageViews: Long) {
        binding.viewListCardItemPageviews.visibility = VISIBLE
        binding.viewListCardItemPageviews.text = StringUtil.getPageViewText(context, pageViews)
    }

    fun setGraphView(viewHistories: List<PageSummary.ViewHistory>) {
        val dataSet = mutableListOf<Float>()
        var i = viewHistories.size
        while (DEFAULT_VIEW_HISTORY_ITEMS > i++) {
            dataSet.add(0f)
        }
        dataSet.addAll(viewHistories.map { it.views })
        binding.viewListCardItemGraph.visibility = VISIBLE
        binding.viewListCardItemGraph.setData(dataSet)
    }

    private fun setViewsGreyedOut(greyedOut: Boolean) {
        val alpha = if (greyedOut) 0.5f else 1.0f
        binding.viewListCardItemTitle.alpha = alpha
        binding.viewListCardItemSubtitle.alpha = alpha
        binding.viewListCardItemImage.alpha = alpha
    }

    companion object {
        private const val DEFAULT_VIEW_HISTORY_ITEMS = 5
    }
}
