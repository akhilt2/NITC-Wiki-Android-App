package org.akhil.nitcwiki.feed.searchbar

import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType

class SearchCard : Card() {
    override fun type(): CardType {
        return CardType.SEARCH_BAR
    }
}
