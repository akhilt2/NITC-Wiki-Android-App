package org.akhil.nitcwiki.feed.announcement

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.core.view.updateLayoutParams
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewCardAnnouncementBinding
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.view.DefaultFeedCardView
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.StringUtil

class AnnouncementCardView(context: Context) : DefaultFeedCardView<AnnouncementCard>(context) {
    interface Callback {
        fun onAnnouncementPositiveAction(card: Card, uri: Uri)
        fun onAnnouncementNegativeAction(card: Card)
    }

    private val binding = ViewCardAnnouncementBinding.inflate(LayoutInflater.from(context), this, true)
    var localCallback: Callback? = null

    init {
        binding.viewAnnouncementText.movementMethod = LinkMovementMethodCompat.getInstance()
        binding.viewAnnouncementFooterText.movementMethod = LinkMovementMethodCompat.getInstance()
        binding.viewAnnouncementActionPositive.setOnClickListener { onPositiveActionClick() }
        binding.viewAnnouncementDialogActionPositive.setOnClickListener { onPositiveActionClick() }
        binding.viewAnnouncementActionNegative.setOnClickListener { onNegativeActionClick() }
        binding.viewAnnouncementDialogActionNegative.setOnClickListener { onNegativeActionClick() }
    }

    override var card: AnnouncementCard? = null
        set(value) {
            field = value
            value?.let {
                if (!it.extract().isNullOrEmpty()) {
                    binding.viewAnnouncementText.text = StringUtil.fromHtml(it.extract())
                }
                if (!it.hasAction()) {
                    binding.viewAnnouncementCardButtonsContainer.visibility = GONE
                } else {
                    binding.viewAnnouncementCardButtonsContainer.visibility = VISIBLE
                    binding.viewAnnouncementActionPositive.text = it.actionTitle()
                    binding.viewAnnouncementDialogActionPositive.text = it.actionTitle()
                }
                if (!it.negativeText().isNullOrEmpty()) {
                    binding.viewAnnouncementActionNegative.text = it.negativeText()
                    binding.viewAnnouncementDialogActionNegative.text = it.negativeText()
                } else {
                    binding.viewAnnouncementActionNegative.visibility = GONE
                    binding.viewAnnouncementDialogActionNegative.visibility = GONE
                }
                if (it.hasImage()) {
                    binding.viewAnnouncementHeaderImage.visibility = VISIBLE
                    binding.viewAnnouncementHeaderImage.loadImage(it.image())
                } else {
                    binding.viewAnnouncementHeaderImage.visibility = GONE
                }
                if (it.imageHeight() > 0) {
                    binding.viewAnnouncementHeaderImage.updateLayoutParams<MarginLayoutParams> {
                        height = DimenUtil.roundedDpToPx(it.imageHeight().toFloat())
                    }
                }
                if (it.hasFooterCaption()) {
                    binding.viewAnnouncementFooterText.text = StringUtil.fromHtml(it.footerCaption())
                } else {
                    binding.viewAnnouncementFooterText.visibility = GONE
                    binding.viewAnnouncementFooterBorder.visibility = GONE
                }
                if (it.hasBorder()) {
                    binding.viewAnnouncementContainer.strokeColor = ContextCompat.getColor(context, R.color.red700)
                    binding.viewAnnouncementContainer.strokeWidth = 10
                } else {
                    binding.viewAnnouncementContainer.setDefaultBorder()
                }
                if (it.isArticlePlacement) {
                    binding.viewAnnouncementCardButtonsContainer.visibility = GONE
                    binding.viewAnnouncementCardDialogButtonsContainer.visibility = VISIBLE
                    binding.viewAnnouncementContainer.layoutParams = LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    binding.viewAnnouncementContainer.radius = 0f
                }
            }
        }

    private fun onPositiveActionClick() {
        card?.let {
            callback?.onAnnouncementPositiveAction(it, it.actionUri())
            localCallback?.onAnnouncementPositiveAction(it, it.actionUri())
        }
    }

    private fun onNegativeActionClick() {
        card?.let {
            callback?.onAnnouncementNegativeAction(it)
            localCallback?.onAnnouncementNegativeAction(it)
        }
    }
}
