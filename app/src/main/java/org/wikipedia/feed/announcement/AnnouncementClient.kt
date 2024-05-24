package org.akhil.nitcwiki.feed.announcement

import android.content.Context
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.FeedCoordinator
import org.akhil.nitcwiki.feed.dataclient.FeedClient
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.GeoUtil
import org.akhil.nitcwiki.util.ReleaseUtil
import org.akhil.nitcwiki.util.log.L
import java.util.Date

class AnnouncementClient : FeedClient {

    private var clientJob: Job? = null

    override fun request(context: Context, wiki: WikiSite, age: Int, cb: FeedClient.Callback) {
        cancel()
        clientJob = CoroutineScope(Dispatchers.Main).launch(
            CoroutineExceptionHandler { _, caught ->
                L.v(caught)
                cb.error(caught)
            }
        ) {
            val announcementsResponse = ServiceFactory.getRest(wiki).getAnnouncements()
            FeedCoordinator.postCardsToCallback(cb, buildCards(announcementsResponse.items))
        }
    }

    override fun cancel() {
        clientJob?.cancel()
    }

    companion object {
        private const val PLATFORM_CODE = "AndroidApp"
        private const val PLATFORM_CODE_NEW = "AndroidAppV2"

        @VisibleForTesting
        private fun buildCards(announcements: List<Announcement>): List<Card> {
            val cards = mutableListOf<Card>()
            val country = GeoUtil.geoIPCountry
            val now = Date()
            for (announcement in announcements) {
                if (shouldShow(announcement, country, now)) {
                    when (announcement.type) {
                        Announcement.SURVEY -> cards.add(SurveyCard(announcement))
                        Announcement.FUNDRAISING -> if (announcement.placement == Announcement.PLACEMENT_FEED) {
                            cards.add(FundraisingCard(announcement))
                        }
                        else -> cards.add(AnnouncementCard(announcement))
                    }
                }
            }
            return cards
        }

        fun shouldShow(announcement: Announcement?, country: String?, date: Date): Boolean {
            return (announcement != null && !announcement.platforms.isNullOrEmpty() && (announcement.platforms.contains(PLATFORM_CODE) ||
                    announcement.platforms.contains(PLATFORM_CODE_NEW)) &&
                    matchesCountryCode(announcement, country) && matchesDate(announcement, date) &&
                    matchesVersionCodes(announcement.minVersion(), announcement.maxVersion()) && matchesConditions(announcement))
        }

        private fun matchesCountryCode(announcement: Announcement, country: String?): Boolean {
            return if (country.isNullOrEmpty() || announcement.countries.isNullOrEmpty()) {
                false
            } else announcement.countries.contains(country)
        }

        private fun matchesDate(announcement: Announcement, date: Date): Boolean {
            if (Prefs.ignoreDateForAnnouncements) {
                return true
            }
            return announcement.startTime()?.before(date) == true && announcement.endTime()?.after(date) == true
        }

        private fun matchesConditions(announcement: Announcement): Boolean {
            if (announcement.beta != null && announcement.beta != ReleaseUtil.isPreProdRelease) {
                return false
            }
            return if (announcement.loggedIn != null && announcement.loggedIn != AccountUtil.isLoggedIn) {
                false
            } else announcement.readingListSyncEnabled == null || announcement.readingListSyncEnabled == Prefs.isReadingListSyncEnabled
        }

        private fun matchesVersionCodes(minVersion: Int, maxVersion: Int): Boolean {
            val versionCode = if (Prefs.announcementsVersionCode > 0) Prefs.announcementsVersionCode
            else WikipediaApp.instance.versionCode
            try {
                if (minVersion != -1 && minVersion > versionCode) {
                    return false
                }
                if (maxVersion != -1 && maxVersion < versionCode) {
                    return false
                }
            } catch (e: NumberFormatException) {
                // ignore
            }
            return true
        }
    }
}
