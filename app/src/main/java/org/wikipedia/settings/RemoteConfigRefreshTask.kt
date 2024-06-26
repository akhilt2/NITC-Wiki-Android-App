package org.akhil.nitcwiki.settings

import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import org.akhil.nitcwiki.dataclient.okhttp.OkHttpConnectionFactory.client
import org.akhil.nitcwiki.recurring.RecurringTask
import org.akhil.nitcwiki.util.log.L
import java.util.*
import java.util.concurrent.TimeUnit

class RemoteConfigRefreshTask : RecurringTask() {
    override val name = "remote-config-refresher"

    override fun shouldRun(lastRun: Date): Boolean {
        return millisSinceLastRun(lastRun) >= TimeUnit.DAYS.toMillis(RUN_INTERVAL_DAYS)
    }

    override fun run(lastRun: Date) {
        var response: Response? = null
        try {
            val request = Request.Builder().url(REMOTE_CONFIG_URL).build()
            response = client.newCall(request).execute()
            val configStr = response.body!!.string()
            RemoteConfig.updateConfig(configStr)
            L.d(configStr)
        } catch (e: Exception) {
            L.e(e)
        } finally {
            response?.closeQuietly()
        }
    }

    companion object {
        private const val REMOTE_CONFIG_URL = "https://meta.wikimedia.org/w/extensions/MobileApp/config/android.json"
        private const val RUN_INTERVAL_DAYS = 1L
    }
}
