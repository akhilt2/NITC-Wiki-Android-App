package org.akhil.nitcwiki.notifications

import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.mwapi.MwQueryResponse
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.DateUtil
import java.util.Date
import java.util.concurrent.TimeUnit

object AnonymousNotificationHelper {
    private const val NOTIFICATION_DURATION_DAYS = 7L

    fun onEditSubmitted() {
        if (!AccountUtil.isLoggedIn) {
            Prefs.lastAnonEditTime = Date().time
        }
    }

    suspend fun observableForAnonUserInfo(wikiSite: WikiSite): MwQueryResponse {
        return if (Date().time - Prefs.lastAnonEditTime < TimeUnit.DAYS.toMillis(NOTIFICATION_DURATION_DAYS)) {
            ServiceFactory.get(wikiSite).getUserInfo()
        } else {
            MwQueryResponse()
        }
    }

    fun shouldCheckAnonNotifications(response: MwQueryResponse): Boolean {
        if (isWithinAnonNotificationTime()) {
            return false
        }
        val hasMessages = response.query?.userInfo?.messages == true
        if (hasMessages) {
            if (!response.query?.userInfo?.name.isNullOrEmpty()) {
                Prefs.lastAnonUserWithMessages = response.query?.userInfo?.name
            }
        }
        return hasMessages
    }

    fun anonTalkPageHasRecentMessage(response: MwQueryResponse, title: PageTitle): Boolean {
        response.query?.firstPage()?.revisions?.firstOrNull()?.timeStamp?.let {
            if (Date().time - DateUtil.iso8601DateParse(it).time < TimeUnit.DAYS.toMillis(NOTIFICATION_DURATION_DAYS)) {
                Prefs.hasAnonymousNotification = true
                Prefs.lastAnonNotificationTime = Date().time
                Prefs.lastAnonNotificationLang = title.wikiSite.languageCode
                return true
            }
        }
        return false
    }

    fun isWithinAnonNotificationTime(): Boolean {
        return Date().time - Prefs.lastAnonNotificationTime < TimeUnit.DAYS.toMillis(NOTIFICATION_DURATION_DAYS)
    }
}
