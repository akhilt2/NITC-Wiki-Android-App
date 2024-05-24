package org.akhil.nitcwiki.readinglist.sync

import android.content.Context
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.notifications.NotificationCategory
import org.akhil.nitcwiki.views.NotificationWithProgressBar

class ReadingListSyncNotification private constructor() {
    private val notification: NotificationWithProgressBar = NotificationWithProgressBar()

    init {
        notification.notificationCategory = NotificationCategory.READING_LIST_SYNCING
        notification.notificationId = NOTIFICATION_ID
        notification.notificationTitle = R.plurals.notification_syncing_reading_list_title
        notification.notificationDescription = R.plurals.notification_syncing_reading_list_description
    }

    fun setNotificationProgress(context: Context, itemsTotal: Int, itemsProgress: Int) {
        notification.setNotificationProgress(context, itemsTotal, itemsProgress)
    }

    fun cancelNotification(context: Context) {
        notification.cancelNotification(context)
    }

    companion object {
        val instance = ReadingListSyncNotification()
        private const val CHANNEL_ID = "READING_LIST_SYNCING_CHANNEL"
        private const val NOTIFICATION_ID = 1002
    }
}
