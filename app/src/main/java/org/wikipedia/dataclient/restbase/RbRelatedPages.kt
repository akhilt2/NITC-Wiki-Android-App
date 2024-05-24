package org.akhil.nitcwiki.dataclient.restbase

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.page.PageSummary

@Serializable
class RbRelatedPages {

    val pages: List<PageSummary>? = null

    fun getPages(limit: Int): MutableList<PageSummary> {
        return pages.orEmpty().take(limit).toMutableList()
    }
}
