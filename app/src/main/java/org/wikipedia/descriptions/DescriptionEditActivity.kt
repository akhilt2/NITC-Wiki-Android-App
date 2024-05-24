package org.akhil.nitcwiki.descriptions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorInt
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.analytics.eventplatform.ABTest.Companion.GROUP_1
import org.akhil.nitcwiki.analytics.eventplatform.MachineGeneratedArticleDescriptionsAnalyticsHelper
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.commons.ImagePreviewDialog
import org.akhil.nitcwiki.extensions.parcelableExtra
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.ExclusiveBottomSheetPresenter
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.page.linkpreview.LinkPreviewDialog
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.suggestededits.PageSummaryForEdit
import org.akhil.nitcwiki.util.DeviceUtil
import org.akhil.nitcwiki.util.ReleaseUtil
import org.akhil.nitcwiki.views.SuggestedArticleDescriptionsDialog

class DescriptionEditActivity : SingleFragmentActivity<DescriptionEditFragment>(), DescriptionEditFragment.Callback {
    enum class Action {
        ADD_DESCRIPTION, TRANSLATE_DESCRIPTION, ADD_CAPTION, TRANSLATE_CAPTION, ADD_IMAGE_TAGS, IMAGE_RECOMMENDATIONS, VANDALISM_PATROL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action = intent.getSerializableExtra(Constants.INTENT_EXTRA_ACTION) as Action
        val pageTitle = intent.parcelableExtra<PageTitle>(Constants.ARG_TITLE)!!

        MachineGeneratedArticleDescriptionsAnalyticsHelper.isUserInExperiment = (ReleaseUtil.isPreBetaRelease && AccountUtil.isLoggedIn &&
                action == Action.ADD_DESCRIPTION && pageTitle.description.isNullOrEmpty() &&
                SuggestedArticleDescriptionsDialog.availableLanguages.contains(pageTitle.wikiSite.languageCode))

        val shouldShowAIOnBoarding = MachineGeneratedArticleDescriptionsAnalyticsHelper.isUserInExperiment &&
                MachineGeneratedArticleDescriptionsAnalyticsHelper.abcTest.group != GROUP_1

        if (action == Action.ADD_DESCRIPTION && Prefs.isDescriptionEditTutorialEnabled) {
            Prefs.isDescriptionEditTutorialEnabled = false
            startActivity(DescriptionEditTutorialActivity.newIntent(this, shouldShowAIOnBoarding))
        }
    }

    public override fun createFragment(): DescriptionEditFragment {
        val invokeSource = intent.getSerializableExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE) as InvokeSource
        val action = intent.getSerializableExtra(Constants.INTENT_EXTRA_ACTION) as Action
        val title = intent.parcelableExtra<PageTitle>(Constants.ARG_TITLE)!!
        return DescriptionEditFragment.newInstance(title,
                intent.getStringExtra(EXTRA_HIGHLIGHT_TEXT),
                intent.parcelableExtra(EXTRA_SOURCE_SUMMARY),
                intent.parcelableExtra(EXTRA_TARGET_SUMMARY),
                action,
                invokeSource)
    }

    override fun onBackPressed() {
        if (fragment.binding.fragmentDescriptionEditView.showingReviewContent()) {
            fragment.binding.fragmentDescriptionEditView.loadReviewContent(false)
        } else {
            DeviceUtil.hideSoftKeyboard(this)
            super.onBackPressed()
        }
    }

    override fun onDescriptionEditSuccess() {
        setResult(DescriptionEditSuccessActivity.RESULT_OK_FROM_EDIT_SUCCESS)
        finish()
    }

    override fun onBottomBarContainerClicked(action: Action) {
        val key = if (action == Action.TRANSLATE_DESCRIPTION) EXTRA_TARGET_SUMMARY else EXTRA_SOURCE_SUMMARY
        val summary = intent.parcelableExtra<PageSummaryForEdit>(key)!!
        if (action == Action.ADD_CAPTION || action == Action.TRANSLATE_CAPTION) {
            ExclusiveBottomSheetPresenter.show(supportFragmentManager,
                    ImagePreviewDialog.newInstance(summary, action))
        } else {
            ExclusiveBottomSheetPresenter.show(supportFragmentManager,
                    LinkPreviewDialog.newInstance(HistoryEntry(summary.pageTitle,
                            if (intent.hasExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE) && intent.getSerializableExtra
                                    (Constants.INTENT_EXTRA_INVOKE_SOURCE) === InvokeSource.PAGE_ACTIVITY)
                                HistoryEntry.SOURCE_EDIT_DESCRIPTION else HistoryEntry.SOURCE_SUGGESTED_EDITS)))
        }
    }

    fun updateStatusBarColor(@ColorInt color: Int) {
        setStatusBarColor(color)
    }

    fun updateNavigationBarColor(@ColorInt color: Int) {
        setNavigationBarColor(color)
    }

    companion object {
        private const val EXTRA_HIGHLIGHT_TEXT = "highlightText"
        private const val EXTRA_SOURCE_SUMMARY = "sourceSummary"
        private const val EXTRA_TARGET_SUMMARY = "targetSummary"

        fun newIntent(context: Context,
                      title: PageTitle,
                      highlightText: String?,
                      sourceSummary: PageSummaryForEdit?,
                      targetSummary: PageSummaryForEdit?,
                      action: Action,
                      invokeSource: InvokeSource): Intent {
            return Intent(context, DescriptionEditActivity::class.java)
                    .putExtra(Constants.ARG_TITLE, title)
                    .putExtra(EXTRA_HIGHLIGHT_TEXT, highlightText)
                    .putExtra(EXTRA_SOURCE_SUMMARY, sourceSummary)
                    .putExtra(EXTRA_TARGET_SUMMARY, targetSummary)
                    .putExtra(Constants.INTENT_EXTRA_ACTION, action)
                    .putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
        }
    }
}
