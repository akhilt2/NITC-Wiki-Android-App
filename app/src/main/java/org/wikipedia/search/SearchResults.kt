package org.akhil.nitcwiki.search

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.mwapi.MwQueryPage
import org.akhil.nitcwiki.dataclient.mwapi.MwQueryResponse

@Serializable
data class SearchResults constructor(var results: MutableList<SearchResult> = mutableListOf(),
                                     var continuation: MwQueryResponse.Continuation? = null) {
    constructor(pages: List<MwQueryPage>, wiki: WikiSite, continuation: MwQueryResponse.Continuation?) : this() {
        // Sort the array based on the "index" property
        results.addAll(pages.sortedBy { it.index }.map { SearchResult(it, wiki, it.coordinates) })
        this.continuation = continuation
    }
}
