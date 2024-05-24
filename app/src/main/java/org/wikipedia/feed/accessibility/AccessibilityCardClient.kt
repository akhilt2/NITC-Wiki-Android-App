package org.akhil.nitcwiki.feed.accessibility

import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.dataclient.DummyClient
import org.akhil.nitcwiki.feed.model.Card

class AccessibilityCardClient : DummyClient() {

    override fun getNewCard(wiki: WikiSite?): Card {
        return AccessibilityCard()
    }
}
