package org.akhil.nitcwiki.feed.offline

import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.dataclient.DummyClient
import org.akhil.nitcwiki.feed.model.Card

class OfflineCardClient : DummyClient() {

    override fun getNewCard(wiki: WikiSite?): Card {
        return OfflineCard()
    }
}
