package org.akhil.nitcwiki.recurring

import android.content.Context
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.analytics.eventplatform.DailyStatsEvent
import org.akhil.nitcwiki.analytics.eventplatform.EventPlatformClient
import java.util.*
import java.util.concurrent.TimeUnit

class DailyEventTask(context: Context) : RecurringTask() {
    override val name = context.getString(R.string.preference_key_daily_event_time_task_name)

    override fun shouldRun(lastRun: Date): Boolean {
        return millisSinceLastRun(lastRun) > TimeUnit.DAYS.toMillis(1)
    }

    override fun run(lastRun: Date) {
        DailyStatsEvent.log(WikipediaApp.instance)
        EventPlatformClient.refreshStreamConfigs()
    }
}
