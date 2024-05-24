package org.akhil.nitcwiki.page

import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.LongPressHandler.WebViewMenuCallback
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.readinglist.ReadingListBehaviorsUtil
import org.akhil.nitcwiki.readinglist.database.ReadingListPage

class PageContainerLongPressHandler(private val fragment: PageFragment) : WebViewMenuCallback {

    override fun onOpenLink(entry: HistoryEntry) {
        fragment.loadPage(entry.title, entry)
    }

    override fun onOpenInNewTab(entry: HistoryEntry) {
        fragment.openInNewBackgroundTab(entry.title, entry)
    }

    override fun onAddRequest(entry: HistoryEntry, addToDefault: Boolean) {
        ReadingListBehaviorsUtil.addToDefaultList(fragment.requireActivity(), entry.title, addToDefault, InvokeSource.CONTEXT_MENU)
    }

    override fun onMoveRequest(page: ReadingListPage?, entry: HistoryEntry) {
        page?.run {
            ReadingListBehaviorsUtil.moveToList(fragment.requireActivity(), this.listId, entry.title, InvokeSource.CONTEXT_MENU)
        }
    }

    override val wikiSite = fragment.title?.wikiSite

    override val referrer = fragment.title?.uri
}
