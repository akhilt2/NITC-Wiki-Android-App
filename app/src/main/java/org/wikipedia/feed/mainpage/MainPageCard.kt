package org.akhil.nitcwiki.feed.mainpage

import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.WikiSiteCard

class MainPageCard(wiki: WikiSite) : WikiSiteCard(wiki) {

    override fun type(): CardType {
        return CardType.MAIN_PAGE
    }
}
