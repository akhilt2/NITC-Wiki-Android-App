package org.akhil.nitcwiki.page

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.history.HistoryEntry

@Serializable
class PageBackStackItem(var title: PageTitle, var historyEntry: HistoryEntry, var scrollY: Int = 0)
