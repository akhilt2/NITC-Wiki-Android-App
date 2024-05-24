package org.akhil.nitcwiki.feed.accessibility

import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType

class AccessibilityCard : Card() {
    override fun type(): CardType {
        return CardType.ACCESSIBILITY
    }
}
