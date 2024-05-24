package org.akhil.nitcwiki.notifications

import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.notifications.db.Notification
import org.akhil.nitcwiki.notifications.db.NotificationDao

class NotificationRepository(private val notificationDao: NotificationDao) {

    fun getAllNotifications() = notificationDao.getAllNotifications()

    private fun insertNotifications(notifications: List<Notification>) {
        notificationDao.insertNotifications(notifications)
    }

    suspend fun updateNotification(notification: Notification) {
        notificationDao.updateNotification(notification)
    }

    suspend fun fetchUnreadWikiDbNames(): Map<String, WikiSite> {
        val response = ServiceFactory.get(Constants.commonsWikiSite).unreadNotificationWikis()
        return response.query?.unreadNotificationWikis!!
            .mapNotNull { (key, wiki) -> wiki.source?.let { key to WikiSite(it.base) } }.toMap()
    }

    suspend fun fetchAndSave(wikiList: String?, filter: String?, continueStr: String? = null): String? {
        var newContinueStr: String? = null
        val response = ServiceFactory.get(WikipediaApp.instance.wikiSite).getAllNotifications(wikiList, filter, continueStr)
        response.query?.notifications?.let {
            insertNotifications(it.list.orEmpty())
            newContinueStr = it.continueStr
        }
        return newContinueStr
    }
}
