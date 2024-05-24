package org.akhil.nitcwiki.feed.announcement

import org.akhil.nitcwiki.feed.model.CardType

class FundraisingCard(announcement: Announcement) : AnnouncementCard(announcement) {

    override fun type(): CardType {
        return CardType.FUNDRAISING
    }
}
