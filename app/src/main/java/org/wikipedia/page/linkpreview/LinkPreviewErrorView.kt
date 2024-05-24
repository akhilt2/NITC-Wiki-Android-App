package org.akhil.nitcwiki.page.linkpreview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewLinkPreviewErrorBinding
import org.akhil.nitcwiki.page.LinkMovementMethodExt
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.StringUtil

class LinkPreviewErrorView : LinearLayout {
    interface Callback {
        fun onAddToList()
        fun onDismiss()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = ViewLinkPreviewErrorBinding.inflate(LayoutInflater.from(context), this)
    var callback: Callback? = null
    val addToListCallback = OverlayViewAddToListCallback()
    val dismissCallback = OverlayViewDismissCallback()

    fun setError(caught: Throwable?, pageTitle: PageTitle) {
        val errorType = LinkPreviewErrorType[caught, pageTitle]
        binding.viewLinkPreviewErrorIcon.setImageResource(errorType.icon)

        if (errorType === LinkPreviewErrorType.OFFLINE) {
            val message = (context.getString(R.string.page_offline_notice_cannot_load_while_offline) +
                    "\n" +
                    context.getString(R.string.page_offline_notice_add_to_reading_list)).trimIndent()
            binding.viewLinkPreviewErrorText.text = message
        } else {
            if (errorType == LinkPreviewErrorType.USER_PAGE_MISSING) {
                binding.viewLinkPreviewErrorText.text = StringUtil.fromHtml(context.getString(errorType.text,
                        pageTitle.uri, pageTitle.displayText, StringUtil.removeNamespace(pageTitle.displayText)))
                binding.viewLinkPreviewErrorText.movementMethod = LinkMovementMethodExt.getExternalLinkMovementMethod()
            } else
                binding.viewLinkPreviewErrorText.text = context.getString(errorType.text)
        }
    }

    inner class OverlayViewAddToListCallback : LinkPreviewOverlayView.Callback {
        override fun onPrimaryClick() {
            callback?.onAddToList()
        }
        override fun onSecondaryClick() {}
        override fun onTertiaryClick() {}
    }

    inner class OverlayViewDismissCallback : LinkPreviewOverlayView.Callback {
        override fun onPrimaryClick() {
            callback?.onDismiss()
        }
        override fun onSecondaryClick() {}
        override fun onTertiaryClick() {}
    }
}
