package org.akhil.nitcwiki.feed.searchbar

import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.dataclient.DummyClient
import org.akhil.nitcwiki.feed.model.Card

class SearchClient : DummyClient() {

    override fun getNewCard(wiki: WikiSite?): Card {
        return SearchCard()
    }
}
