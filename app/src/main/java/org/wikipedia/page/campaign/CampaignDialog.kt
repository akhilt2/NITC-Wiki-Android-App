package org.akhil.nitcwiki.page.campaign

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.activity.BaseActivity
import org.akhil.nitcwiki.analytics.eventplatform.DonorExperienceEvent
import org.akhil.nitcwiki.dataclient.donate.Campaign
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.CustomTabsUtil
import org.akhil.nitcwiki.util.FeedbackUtil
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date

class CampaignDialog internal constructor(private val context: Context, val campaign: Campaign) : AlertDialog.Builder(context), CampaignDialogView.Callback {

    private var dialog: AlertDialog? = null

    init {
        val campaignView = CampaignDialogView(context)
        campaignView.campaignAssets = campaign.assets[WikipediaApp.instance.appOrSystemLanguageCode]
        campaignView.callback = this
        val dateDiff = Duration.between(Instant.ofEpochMilli(Prefs.announcementPauseTime), Instant.now())
        campaignView.showNeutralButton = dateDiff.toDays() >= 1 && campaign.endDateTime?.isAfter(LocalDateTime.now().plusDays(1)) == true
        campaignView.setupViews()
        setView(campaignView)
    }

    override fun show(): AlertDialog {
        dialog = super.show()
        return dialog!!
    }

    private fun dismissDialog(skipCampaign: Boolean = true) {
        // "Maybe later" option will show up the campaign after one day.
        if (skipCampaign) {
            Prefs.announcementShownDialogs = setOf(campaign.id)
        }
        dialog?.dismiss()
    }

    override fun onPositiveAction(url: String) {
        DonorExperienceEvent.logAction("donate_start_click", "article_banner", campaignId = campaign.id)
        val customTabUrl = Prefs.announcementCustomTabTestUrl.orEmpty().ifEmpty { url }
        if (context is BaseActivity) {
            context.launchDonateDialog(campaign.id, customTabUrl)
            dismissDialog(false)
        } else {
            CustomTabsUtil.openInCustomTab(context, customTabUrl)
            dismissDialog()
        }
    }

    override fun onNegativeAction() {
        DonorExperienceEvent.logAction("already_donated_click", "article_banner", campaignId = campaign.id)
        FeedbackUtil.showMessage(context as Activity, R.string.donation_campaign_donated_snackbar)
        dismissDialog()
    }

    override fun onNeutralAction() {
        DonorExperienceEvent.logAction("later_click", "article_banner", campaignId = campaign.id)
        Prefs.announcementPauseTime = Date().time
        FeedbackUtil.showMessage(context as Activity, R.string.donation_campaign_maybe_later_snackbar)
        DonorExperienceEvent.logAction("reminder_toast", "article_banner", campaignId = campaign.id)
        dismissDialog(false)
    }

    override fun onClose() {
        DonorExperienceEvent.logAction("close_click", "article_banner", campaignId = campaign.id)
        dismissDialog()
    }
}
