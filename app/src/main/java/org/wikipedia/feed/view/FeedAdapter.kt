package org.akhil.nitcwiki.feed.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import org.akhil.nitcwiki.feed.FeedCoordinatorBase
import org.akhil.nitcwiki.feed.accessibility.AccessibilityCard
import org.akhil.nitcwiki.feed.announcement.AnnouncementCardView
import org.akhil.nitcwiki.feed.dayheader.DayHeaderCardView
import org.akhil.nitcwiki.feed.image.FeaturedImageCardView
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.news.NewsCardView
import org.akhil.nitcwiki.feed.offline.OfflineCard
import org.akhil.nitcwiki.feed.offline.OfflineCardView
import org.akhil.nitcwiki.feed.random.RandomCardView
import org.akhil.nitcwiki.feed.searchbar.SearchCardView
import org.akhil.nitcwiki.feed.suggestededits.SuggestedEditsCardView
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.views.DefaultRecyclerAdapter
import org.akhil.nitcwiki.views.DefaultViewHolder

@Suppress("UNCHECKED_CAST")
class FeedAdapter<T : View>(private val coordinator: FeedCoordinatorBase, private val callback: Callback?) :
    DefaultRecyclerAdapter<Card?, T>(coordinator.cards) {

    interface Callback : ListCardItemView.Callback, CardHeaderView.Callback,
        FeaturedImageCardView.Callback, SearchCardView.Callback, NewsCardView.Callback,
        AnnouncementCardView.Callback, RandomCardView.Callback, ListCardView.Callback,
        SuggestedEditsCardView.Callback {
        fun onRequestMore()
        fun onRetryFromOffline()
        fun onError(t: Throwable)
    }

    private var feedView: FeedView? = null
    private var lastCardReloadTrigger: Card? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder<T> {
        return DefaultViewHolder(newView(parent.context, viewType))
    }

    override fun onBindViewHolder(holder: DefaultViewHolder<T>, position: Int) {
        val item = item(position)
        val view = holder.view as FeedCardView<Card>
        lastCardReloadTrigger = if (coordinator.finished() &&
            position == itemCount - 1 && item !is OfflineCard && item !is AccessibilityCard &&
            item !== lastCardReloadTrigger && callback != null) {
            callback.onRequestMore()
            item
        } else {
            null
        }
        view.card = item
        if (view is OfflineCardView && position == 1) {
            view.setTopPadding()
        }
    }

    override fun onViewAttachedToWindow(holder: DefaultViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
        if (holder.view is SearchCardView) {
            adjustSearchView(holder.view as SearchCardView)
        } else if (holder.view is DayHeaderCardView) {
            adjustDayHeaderView(holder.view as DayHeaderCardView)
        }
        (holder.view as FeedCardView<*>).callback = callback
    }

    override fun onViewDetachedFromWindow(holder: DefaultViewHolder<T>) {
        (holder.view as FeedCardView<*>).callback = null
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return item(position)!!.type().code()
    }

    private fun newView(context: Context, viewType: Int): T {
        return CardType.of(viewType).newView(context) as T
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        feedView = recyclerView as FeedView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        feedView = null
    }

    private fun adjustSearchView(view: SearchCardView) {
        view.updateLayoutParams<StaggeredGridLayoutManager.LayoutParams> {
            isFullSpan = true
            bottomMargin = DimenUtil.roundedDpToPx(8F)
            if (DimenUtil.isLandscape(view.context)) {
                val margin = (view.parent as View).width / 6
                updateMargins(left = margin, right = margin)
            }
        }
    }

    private fun adjustDayHeaderView(view: DayHeaderCardView) {
        view.updateLayoutParams<StaggeredGridLayoutManager.LayoutParams> { isFullSpan = true }
    }
}
