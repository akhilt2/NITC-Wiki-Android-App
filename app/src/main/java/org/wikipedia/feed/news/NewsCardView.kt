package org.akhil.nitcwiki.feed.news

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewCardNewsBinding
import org.akhil.nitcwiki.feed.view.DefaultFeedCardView
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.L10nUtil
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.views.ImageZoomHelper

class NewsCardView(context: Context) : DefaultFeedCardView<NewsCard>(context) {

    interface Callback {
        fun onNewsItemSelected(card: NewsCard, view: NewsItemView)
    }

    private val binding = ViewCardNewsBinding.inflate(LayoutInflater.from(context), this, true)
    private var prevImageDownloadSettings = Prefs.isImageDownloadEnabled
    private var isSnapHelperAttached = false

    private fun setUpIndicatorDots(card: NewsCard) {
        val indicatorRadius = 4
        val indicatorPadding = 8
        val indicatorHeight = 20
        binding.newsCardviewRecyclerView.addItemDecoration(
            RecyclerViewIndicatorDotDecor(
                DimenUtil.roundedDpToPx(indicatorRadius.toFloat()).toFloat(),
                DimenUtil.roundedDpToPx(indicatorPadding.toFloat()),
                DimenUtil.roundedDpToPx(indicatorHeight.toFloat()),
                ResourceUtil.getThemedColor(context, R.attr.border_color),
                ResourceUtil.getThemedColor(context, R.attr.progressive_color),
                L10nUtil.isLangRTL(card.wikiSite().languageCode)
            )
        )
    }

    override var callback: FeedAdapter.Callback? = null
        set(value) {
            field = value
            binding.headerView.setCallback(value)
        }

    override var card: NewsCard? = null
        set(value) {
            if (field != value || prevImageDownloadSettings != Prefs.isImageDownloadEnabled) {
                field = value
                prevImageDownloadSettings = Prefs.isImageDownloadEnabled
                value?.let {
                    header(it)
                    setLayoutDirectionByWikiSite(it.wikiSite(), binding.rtlContainer)
                    setUpRecycler(it)
                }
            }
        }

    private fun setUpRecycler(card: NewsCard) {
        binding.newsCardviewRecyclerView.setHasFixedSize(true)
        binding.newsCardviewRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.newsCardviewRecyclerView.isNestedScrollingEnabled = false
        binding.newsCardviewRecyclerView.clipToPadding = false
        binding.newsCardviewRecyclerView.adapter = NewsAdapter(card)
        setUpIndicatorDots(card)
        setUpSnapHelper()
    }

    private fun setUpSnapHelper() {
        if (!isSnapHelperAttached) {
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding.newsCardviewRecyclerView)
            isSnapHelperAttached = true
        }
    }

    private fun header(card: NewsCard) {
        binding.headerView.setTitle(card.title())
            .setLangCode(card.wikiSite().languageCode)
            .setCard(card)
    }

    private class NewsItemHolder constructor(private val newsItemView: NewsItemView) : RecyclerView.ViewHolder(newsItemView) {
        fun bindItem(newsItem: NewsItem) {
            newsItemView.setContents(newsItem)
        }

        val view get() = newsItemView
    }

    private inner class NewsAdapter constructor(private val card: NewsCard) : RecyclerView.Adapter<NewsItemHolder>() {

        override fun getItemCount(): Int {
            return card.news().size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemHolder {
            return NewsItemHolder(NewsItemView(context))
        }

        override fun onBindViewHolder(holder: NewsItemHolder, position: Int) {
            holder.bindItem(card.news()[position])
            holder.view.setOnClickListener {
                callback?.onNewsItemSelected(card, holder.view)
            }
            holder.view.setOnLongClickListener {
                if (ImageZoomHelper.isZooming) {
                    ImageZoomHelper.dispatchCancelEvent(holder.view)
                }
                true
            }
        }

        override fun onViewAttachedToWindow(holder: NewsItemHolder) {
            super.onViewAttachedToWindow(holder)
            holder.view.callback = callback
        }

        override fun onViewDetachedFromWindow(holder: NewsItemHolder) {
            holder.view.callback = null
            super.onViewDetachedFromWindow(holder)
        }
    }
}
