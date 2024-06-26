package org.akhil.nitcwiki.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewNotificationButtonBinding
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ResourceUtil

class NotificationButtonView constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private val binding = ViewNotificationButtonBinding.inflate(LayoutInflater.from(context), this)

    init {
        layoutParams = ViewGroup.LayoutParams(DimenUtil.roundedDpToPx(48.0f), ViewGroup.LayoutParams.MATCH_PARENT)
        setBackgroundResource(ResourceUtil.getThemedAttributeId(context, androidx.appcompat.R.attr.selectableItemBackgroundBorderless))
        isFocusable = true
    }

    fun setUnreadCount(count: Int) {
        binding.unreadDot.setUnreadCount(count)
        binding.unreadDot.isVisible = count > 0
    }

    fun runAnimation() {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.tab_list_zoom_enter))
    }

    fun setIcon(@DrawableRes icon: Int) {
        binding.iconImage.setImageResource(icon)
    }
}
