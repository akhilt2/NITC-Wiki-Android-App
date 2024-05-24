package org.akhil.nitcwiki.events

import org.akhil.nitcwiki.readinglist.database.ReadingListPage

class ArticleSavedOrDeletedEvent(val isAdded: Boolean, vararg pages: ReadingListPage) {
    val pages = pages.toList()
}
