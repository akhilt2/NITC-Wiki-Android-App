package org.akhil.nitcwiki.feed.news

import android.content.Context
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.getSpans
import org.akhil.nitcwiki.databinding.ViewHorizontalScrollListItemCardBinding
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.richtext.RichTextUtil
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.log.L
import org.akhil.nitcwiki.views.ImageZoomHelper
import org.akhil.nitcwiki.views.WikiCardView

class NewsItemView(context: Context) : WikiCardView(context) {

    private val binding = ViewHorizontalScrollListItemCardBinding.inflate(LayoutInflater.from(context), this, true)
    var callback: FeedAdapter.Callback? = null
    var newsItem: NewsItem? = null

    init {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val margin = DimenUtil.roundedDpToPx(8f)
        layoutParams.setMargins(margin, margin, margin, margin)
        setLayoutParams(layoutParams)
    }

    /* Remove the in-Wikitext thumbnail caption, which will almost certainly not apply here */
    private fun removeImageCaption(text: Spanned): CharSequence {
        val spans = text.getSpans<Any>()
        val span = spans.find { it is StyleSpan && it.style == Typeface.ITALIC }
        span?.let {
            val start = text.getSpanStart(it)
            val end = text.getSpanEnd(it)
            if (text[start] == '(' && text[end - 1] == ')') {
                L.v("Removing spanned text: " + text.subSequence(start, end))
                return RichTextUtil.remove(text, start, end)
            }
        }
        return text
    }

    fun setContents(newsItem: NewsItem) {
        this.newsItem = newsItem
        binding.horizontalScrollListItemText.text = removeImageCaption(StringUtil.fromHtml(newsItem.story))
        RichTextUtil.removeUnderlinesFromLinksAndMakeBold(binding.horizontalScrollListItemText)
        newsItem.thumb()?.let {
            binding.horizontalScrollListItemImageContainer.visibility = VISIBLE
            binding.horizontalScrollListItemImage.loadImage(it)
            ImageZoomHelper.setViewZoomable(binding.horizontalScrollListItemImage)
        } ?: run {
            binding.horizontalScrollListItemImageContainer.visibility = GONE
            binding.horizontalScrollListItemText.maxLines = 10
        }
    }

    val imageView = binding.horizontalScrollListItemImage
}
