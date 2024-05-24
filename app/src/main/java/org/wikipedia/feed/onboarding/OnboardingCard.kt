package org.akhil.nitcwiki.feed.onboarding

import androidx.annotation.StringRes
import org.akhil.nitcwiki.feed.announcement.Announcement
import org.akhil.nitcwiki.feed.announcement.AnnouncementCard
import org.akhil.nitcwiki.settings.PrefsIoUtil

abstract class OnboardingCard(announcement: Announcement) : AnnouncementCard(announcement) {

    @StringRes abstract fun prefKey(): Int

    open fun shouldShow(): Boolean {
        return PrefsIoUtil.getBoolean(prefKey(), true)
    }

    override fun onDismiss() {
        PrefsIoUtil.setBoolean(prefKey(), false)
    }

    override fun onRestore() {
        PrefsIoUtil.setBoolean(prefKey(), true)
    }
}
