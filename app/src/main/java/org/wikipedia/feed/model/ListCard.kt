package org.akhil.nitcwiki.feed.model

import org.akhil.nitcwiki.dataclient.WikiSite
import java.util.*

abstract class ListCard<T : Card>(private val items: List<T>,
                                  wiki: WikiSite) : WikiSiteCard(wiki) {

    fun items(): List<T> {
        return Collections.unmodifiableList(items)
    }
}
