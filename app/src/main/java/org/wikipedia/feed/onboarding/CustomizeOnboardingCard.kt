package org.akhil.nitcwiki.feed.onboarding

import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.feed.announcement.Announcement
import org.akhil.nitcwiki.feed.model.CardType

class CustomizeOnboardingCard(announcement: Announcement) : OnboardingCard(announcement) {

    override fun type(): CardType {
        return CardType.ONBOARDING_CUSTOMIZE_FEED
    }

    override fun shouldShow(): Boolean {
        return super.shouldShow() && WikipediaApp.instance.isOnline
    }

    override fun prefKey(): Int {
        return R.string.preference_key_feed_customize_onboarding_card_enabled
    }
}
