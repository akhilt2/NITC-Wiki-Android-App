package org.akhil.nitcwiki.page.campaign

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.core.view.isVisible
import org.akhil.nitcwiki.analytics.eventplatform.DonorExperienceEvent
import org.akhil.nitcwiki.databinding.DialogCampaignBinding
import org.akhil.nitcwiki.dataclient.donate.Campaign
import org.akhil.nitcwiki.page.LinkMovementMethodExt
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.UriUtil

class CampaignDialogView(context: Context) : FrameLayout(context) {
    interface Callback {
        fun onPositiveAction(url: String)
        fun onNegativeAction()
        fun onNeutralAction()
        fun onClose()
    }

    private val binding = DialogCampaignBinding.inflate(LayoutInflater.from(context), this, true)
    var showNeutralButton = true
    var campaignAssets: Campaign.Assets? = null
    var callback: Callback? = null

    fun setupViews() {
        campaignAssets?.let {
            if (!it.text.isNullOrEmpty()) {
                binding.contentText.movementMethod = LinkMovementMethodCompat.getInstance()
                binding.contentText.text = StringUtil.fromHtml(it.text)
            }
            if (!it.footer.isNullOrEmpty()) {
                binding.footerText.movementMethod = LinkMovementMethodExt { url: String ->
                    DonorExperienceEvent.logAction("donor_policy_click", "article_banner")
                    UriUtil.visitInExternalBrowser(this.context, Uri.parse(url))
                }
                binding.footerText.text = StringUtil.fromHtml(it.footer)
            }

            binding.buttonsContainer.isVisible = true

            binding.closeButton.setOnClickListener {
                callback?.onClose()
            }
            FeedbackUtil.setButtonTooltip(binding.closeButton)

            // TODO: think about optimizing the usage of actions array
            try {
                if (it.actions.size >= 3) {
                    val positiveButton = it.actions[0]
                    val neutralButton = it.actions[1]
                    val negativeButton = it.actions[2]

                    binding.positiveButton.text = positiveButton.title
                    positiveButton.url?.let { url ->
                        binding.positiveButton.setOnClickListener {
                            callback?.onPositiveAction(url)
                        }
                    }

                    binding.neutralButton.text = neutralButton.title
                    binding.neutralButton.isVisible = showNeutralButton
                    binding.neutralButton.setOnClickListener {
                        callback?.onNeutralAction()
                    }

                    binding.negativeButton.text = negativeButton.title
                    binding.negativeButton.setOnClickListener {
                        callback?.onNegativeAction()
                    }
                }
            } catch (e: Exception) {
                binding.buttonsContainer.isVisible = false
            }
        }
    }
}
