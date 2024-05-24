package org.akhil.nitcwiki.page

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.database.AppDatabase
import org.akhil.nitcwiki.readinglist.database.ReadingListPage
import org.akhil.nitcwiki.util.log.L

object PageAvailableOfflineHandler {
    fun interface Callback {
        fun onFinish(available: Boolean)
    }

    fun check(page: ReadingListPage, callback: Callback) {
        callback.onFinish(WikipediaApp.instance.isOnline || (page.offline && !page.saving))
    }

    fun check(lifeCycleScope: CoroutineScope, pageTitle: PageTitle, callback: Callback) {
        if (WikipediaApp.instance.isOnline) {
            callback.onFinish(true)
            return
        }
        lifeCycleScope.launch(CoroutineExceptionHandler { _, exception ->
            callback.onFinish(false)
            L.w(exception)
        }) {
            val readingListPage = AppDatabase.instance.readingListPageDao().findPageInAnyList(pageTitle)
            callback.onFinish(readingListPage != null && readingListPage.offline && !readingListPage.saving)
        }
    }
}
