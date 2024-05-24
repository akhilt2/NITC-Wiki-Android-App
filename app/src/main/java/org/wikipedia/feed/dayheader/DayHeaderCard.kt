package org.akhil.nitcwiki.feed.dayheader

import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.util.DateUtil

class DayHeaderCard(private val age: Int) : Card() {

    override fun title(): String {
        return DateUtil.getFeedCardDateString(age)
    }

    override fun type(): CardType {
        return CardType.DAY_HEADER
    }
}
