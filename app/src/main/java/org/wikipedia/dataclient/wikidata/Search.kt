package org.akhil.nitcwiki.dataclient.wikidata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.mwapi.MwResponse

@Serializable
class Search : MwResponse() {

    @SerialName("search")
    val results: List<SearchResult> = emptyList()

    @Serializable
    class SearchResult {
        val id: String = ""
        val label: String = ""
        val description: String = ""
    }
}
