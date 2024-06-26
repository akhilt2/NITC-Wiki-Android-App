package org.akhil.nitcwiki.edit

import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.analytics.eventplatform.PatrollerExperienceEvent
import org.akhil.nitcwiki.edit.insertmedia.InsertMediaActivity
import org.akhil.nitcwiki.edit.templates.TemplatesSearchActivity
import org.akhil.nitcwiki.extensions.parcelableExtra
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.ExclusiveBottomSheetPresenter
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.page.linkpreview.LinkPreviewDialog
import org.akhil.nitcwiki.search.SearchActivity
import org.akhil.nitcwiki.util.DeviceUtil
import org.akhil.nitcwiki.util.DimenUtil

class SyntaxHighlightViewAdapter(
    val activity: AppCompatActivity,
    val pageTitle: PageTitle,
    private val rootView: View,
    val editText: SyntaxHighlightableEditText,
    private val wikiTextKeyboardView: WikiTextKeyboardView,
    private val wikiTextKeyboardFormattingView: WikiTextKeyboardFormattingView,
    private val wikiTextKeyboardHeadingsView: WikiTextKeyboardHeadingsView,
    private val invokeSource: Constants.InvokeSource,
    private val requestInsertMedia: ActivityResultLauncher<Intent>,
    private val isFromDiff: Boolean = false,
    showUserMention: Boolean = false
) : WikiTextKeyboardView.Callback {

    init {
        wikiTextKeyboardView.editText = editText
        wikiTextKeyboardView.callback = this
        wikiTextKeyboardFormattingView.editText = editText
        wikiTextKeyboardFormattingView.callback = this
        wikiTextKeyboardHeadingsView.editText = editText
        wikiTextKeyboardHeadingsView.callback = this
        wikiTextKeyboardView.userMentionVisible = showUserMention
        hideAllSyntaxModals()

        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            activity.window.decorView.post {
                if (!activity.isDestroyed) {
                    showOrHideSyntax(editText.hasFocus())
                }
            }
        }

        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            showOrHideSyntax(hasFocus)
        }
    }

    private val requestLinkFromSearch = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == SearchActivity.RESULT_LINK_SUCCESS) {
            it.data?.parcelableExtra<PageTitle>(SearchActivity.EXTRA_RETURN_LINK_TITLE)?.let { title ->
                wikiTextKeyboardView.insertLink(title, pageTitle.wikiSite.languageCode)
            }
        }
    }

    private val requestInsertTemplate = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == TemplatesSearchActivity.RESULT_INSERT_TEMPLATE_SUCCESS) {
            it.data?.let { data ->
                val newWikiText = data.getStringExtra(TemplatesSearchActivity.RESULT_WIKI_TEXT)
                editText.inputConnection?.commitText(newWikiText, 1)
            }
        }
    }

    override fun onPreviewLink(title: String) {
        val dialog = LinkPreviewDialog.newInstance(HistoryEntry(PageTitle(title, pageTitle.wikiSite), HistoryEntry.SOURCE_INTERNAL_LINK))
        ExclusiveBottomSheetPresenter.show(activity.supportFragmentManager, dialog)
        editText.post {
            dialog.dialog?.setOnDismissListener {
                if (!activity.isDestroyed) {
                    editText.postDelayed({
                        DeviceUtil.showSoftKeyboard(editText)
                    }, 200)
                }
            }
        }
    }

    override fun onRequestInsertMedia() {
        requestInsertMedia.launch(InsertMediaActivity.newIntent(activity, pageTitle.wikiSite,
            if (invokeSource == Constants.InvokeSource.EDIT_ACTIVITY) pageTitle.displayText else "",
            invokeSource))
    }

    override fun onRequestInsertTemplate() {
        if (isFromDiff) {
            val activeInterface = if (invokeSource == Constants.InvokeSource.TALK_REPLY_ACTIVITY) "pt_talk" else "pt_edit"
            PatrollerExperienceEvent.logAction("template_init", activeInterface)
        }
        requestInsertTemplate.launch(TemplatesSearchActivity.newIntent(activity, pageTitle.wikiSite, isFromDiff, invokeSource))
    }

    override fun onRequestInsertLink() {
        requestLinkFromSearch.launch(SearchActivity.newIntent(activity, invokeSource, null, true))
    }

    override fun onRequestHeading() {
        if (wikiTextKeyboardHeadingsView.isVisible) {
            hideAllSyntaxModals()
            return
        }
        hideAllSyntaxModals()
        wikiTextKeyboardHeadingsView.isVisible = true
        wikiTextKeyboardView.onAfterHeadingsShown()
    }

    override fun onRequestFormatting() {
        if (wikiTextKeyboardFormattingView.isVisible) {
            hideAllSyntaxModals()
            return
        }
        hideAllSyntaxModals()
        wikiTextKeyboardFormattingView.isVisible = true
        wikiTextKeyboardView.onAfterFormattingShown()
    }

    override fun onSyntaxOverlayCollapse() {
        hideAllSyntaxModals()
    }

    private fun hideAllSyntaxModals() {
        wikiTextKeyboardHeadingsView.isVisible = false
        wikiTextKeyboardFormattingView.isVisible = false
        wikiTextKeyboardView.onAfterOverlaysHidden()
    }

    private fun showOrHideSyntax(hasFocus: Boolean) {
        val hasMinHeight = DeviceUtil.isHardKeyboardAttached(activity.resources) ||
                activity.window.decorView.height - rootView.height > DimenUtil.roundedDpToPx(150f)
        if (hasFocus && hasMinHeight) {
            wikiTextKeyboardView.isVisible = true
        } else {
            hideAllSyntaxModals()
            wikiTextKeyboardView.isVisible = false
        }
    }
}
