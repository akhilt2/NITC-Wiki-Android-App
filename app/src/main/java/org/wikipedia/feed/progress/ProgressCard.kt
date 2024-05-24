package org.akhil.nitcwiki.feed.progress

import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType

class ProgressCard : Card() {

    override fun type(): CardType {
        return CardType.PROGRESS
    }
}
