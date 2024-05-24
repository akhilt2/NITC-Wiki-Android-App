package org.akhil.nitcwiki.feed.offline

import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType

class OfflineCard : Card() {

    override fun type(): CardType {
        return CardType.OFFLINE
    }
}
