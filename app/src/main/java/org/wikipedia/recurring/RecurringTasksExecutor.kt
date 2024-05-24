package org.akhil.nitcwiki.recurring

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.alphaupdater.AlphaUpdateChecker
import org.akhil.nitcwiki.settings.RemoteConfigRefreshTask
import org.akhil.nitcwiki.util.ReleaseUtil

class RecurringTasksExecutor(private val app: WikipediaApp) {
    fun run() {
        Completable.fromAction {
            val allTasks = arrayOf( // Has list of all rotating tasks that need to be run
                    RemoteConfigRefreshTask(),
                    DailyEventTask(app),
                    TalkOfflineCleanupTask(app)
            )
            for (task in allTasks) {
                task.runIfNecessary()
            }
            if (ReleaseUtil.isAlphaRelease) {
                AlphaUpdateChecker(app).runIfNecessary()
            }
        }.subscribeOn(Schedulers.io()).subscribe()
    }
}
