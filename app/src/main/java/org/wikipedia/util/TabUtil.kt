package org.akhil.nitcwiki.util

import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.PageBackStackItem
import org.akhil.nitcwiki.page.tabs.Tab

object TabUtil {

    fun openInNewBackgroundTab(entry: HistoryEntry) {
        val app = WikipediaApp.instance
        val tab = if (app.tabCount == 0) app.tabList[0] else Tab()
        if (app.tabCount > 0) {
            app.tabList.add(0, tab)
            while (app.tabList.size > Constants.MAX_TABS) {
                app.tabList.removeAt(0)
            }
        }
        tab.backStack.add(PageBackStackItem(entry.title, entry))
        app.commitTabState()
    }
}
