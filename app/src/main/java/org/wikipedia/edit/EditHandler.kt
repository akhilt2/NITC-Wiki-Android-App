package org.akhil.nitcwiki.edit

import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.bridge.CommunicationBridge
import org.akhil.nitcwiki.bridge.CommunicationBridge.JSEventListener
import org.akhil.nitcwiki.descriptions.DescriptionEditUtil
import org.akhil.nitcwiki.page.Page
import org.akhil.nitcwiki.page.PageFragment
import org.akhil.nitcwiki.util.log.L

class EditHandler(private val fragment: PageFragment, bridge: CommunicationBridge) : JSEventListener {

    private var currentPage: Page? = null

    init {
        bridge.addListener(TYPE_EDIT_SECTION, this)
        bridge.addListener(TYPE_ADD_TITLE_DESCRIPTION, this)
    }

    override fun onMessage(messageType: String, messagePayload: JsonObject?) {
        if (!fragment.isAdded) {
            return
        }

        currentPage?.let {
            if (messageType == TYPE_EDIT_SECTION) {
                val sectionId = messagePayload?.run { this[PAYLOAD_SECTION_ID]?.jsonPrimitive?.int } ?: 0
                if (sectionId == 0 && DescriptionEditUtil.isEditAllowed(it) && it.isArticle) {
                    val tempView = View(fragment.requireContext())
                    tempView.x = fragment.webView.touchStartX
                    tempView.y = fragment.webView.touchStartY
                    (fragment.view as ViewGroup).addView(tempView)
                    val menu = PopupMenu(fragment.requireContext(), tempView, 0, 0, R.style.PagePopupMenu)
                    menu.menuInflater.inflate(R.menu.menu_page_header_edit, menu.menu)
                    menu.setOnMenuItemClickListener(EditMenuClickListener())
                    menu.setOnDismissListener { (fragment.view as? ViewGroup)?.removeView(tempView) }
                    menu.show()
                } else {
                    startEditingSection(sectionId, null)
                }
            } else if (messageType == TYPE_ADD_TITLE_DESCRIPTION && DescriptionEditUtil.isEditAllowed(it)) {
                fragment.verifyBeforeEditingDescription(null, Constants.InvokeSource.PAGE_DESCRIPTION_CTA)
            }
        }
    }

    private inner class EditMenuClickListener : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.menu_page_header_edit_description -> {
                    fragment.verifyBeforeEditingDescription(null, Constants.InvokeSource.PAGE_EDIT_PENCIL)
                    true
                }
                R.id.menu_page_header_edit_lead_section -> {
                    startEditingSection(0, null)
                    true
                }
                else -> false
            }
        }
    }

    fun setPage(page: Page?) {
        page?.let {
            currentPage = it
        }
    }

    fun startEditingSection(sectionID: Int, highlightText: String?) {
        currentPage?.let {
            if (sectionID < 0 || sectionID >= it.sections.size) {
                L.w("Attempting to edit a mismatched section ID.")
                return
            }
            fragment.onRequestEditSection(it.sections[sectionID].id, it.sections[sectionID].anchor, it.title, highlightText)
        }
    }

    fun startEditingArticle() {
        currentPage?.let {
            fragment.onRequestEditSection(-1, null, it.title, null)
        }
    }

    companion object {
        private const val TYPE_EDIT_SECTION = "edit_section"
        private const val TYPE_ADD_TITLE_DESCRIPTION = "add_title_description"
        private const val PAYLOAD_SECTION_ID = "sectionId"
        const val RESULT_REFRESH_PAGE = 1
    }
}
