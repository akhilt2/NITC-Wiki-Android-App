package org.akhil.nitcwiki.readinglist

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewReadingListHeaderBinding
import org.akhil.nitcwiki.readinglist.database.ReadingList
import org.akhil.nitcwiki.util.GradientUtil
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.views.ViewUtil

class ReadingListHeaderView : FrameLayout {

    private val binding = ViewReadingListHeaderBinding.inflate(LayoutInflater.from(context), this)
    private var imageViews = listOf(binding.readingListHeaderImage0, binding.readingListHeaderImage1, binding.readingListHeaderImage2,
            binding.readingListHeaderImage3, binding.readingListHeaderImage4, binding.readingListHeaderImage5)
    private var readingList: ReadingList? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        if (!isInEditMode) {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            binding.readingListHeaderImageGradient.background = GradientUtil.getPowerGradient(
                ResourceUtil.getThemedColor(context, R.attr.overlay_color), Gravity.TOP)
            clearThumbnails()
        }
    }

    fun setReadingList(readingList: ReadingList) {
        this.readingList = readingList
        if (readingList.pages.isEmpty()) {
            binding.readingListHeaderImageContainer.visibility = GONE
            binding.readingListHeaderEmptyImage.visibility = VISIBLE
        } else {
            binding.readingListHeaderImageContainer.visibility = VISIBLE
            binding.readingListHeaderEmptyImage.visibility = GONE
            updateThumbnails()
        }
    }

    private fun clearThumbnails() {
        imageViews.forEach {
            ViewUtil.loadImage(it, null)
        }
    }

    private fun updateThumbnails() {
        readingList?.let {
            clearThumbnails()
            val thumbUrls = it.pages.mapNotNull { page -> page.thumbUrl }
                .filterNot { url -> url.isEmpty() }
            (imageViews zip thumbUrls).forEach { (imageView, url) ->
                ViewUtil.loadImage(imageView, url)
            }
        }
    }
}
