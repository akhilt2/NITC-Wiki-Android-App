package org.akhil.nitcwiki.feed.model

import org.akhil.nitcwiki.dataclient.WikiSite

abstract class WikiSiteCard(val wiki: WikiSite) : Card() {

    fun wikiSite(): WikiSite {
        return wiki
    }
}
