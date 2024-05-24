package org.akhil.nitcwiki.analytics.metricsplatform

import android.os.Build
import org.wikimedia.metrics_platform.MetricsClient
import org.wikimedia.metrics_platform.context.AgentData
import org.wikimedia.metrics_platform.context.ClientData
import org.wikimedia.metrics_platform.context.MediawikiData
import org.akhil.nitcwiki.BuildConfig
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.ReleaseUtil
import java.time.Duration

object MetricsPlatform {
    val agentData = AgentData(
        BuildConfig.FLAVOR + BuildConfig.BUILD_TYPE,
        WikipediaApp.instance.appInstallID,
        WikipediaApp.instance.currentTheme.toString(),
        WikipediaApp.instance.versionCode,
        "WikipediaApp/" + BuildConfig.VERSION_NAME,
        "android",
        "app",
        Build.BRAND + " " + Build.MODEL,
        WikipediaApp.instance.languageState.systemLanguageCode,
        if (ReleaseUtil.isProdRelease) "prod" else "dev"
    )

    val mediawikiData = MediawikiData(
        WikipediaApp.instance.wikiSite.dbName(),
    )

    val domain = WikipediaApp.instance.wikiSite.authority()

    private val clientData = ClientData(
        agentData,
        null,
        mediawikiData,
        null,
        domain
    )

    val client: MetricsClient = MetricsClient.builder(clientData)
        .eventQueueCapacity(Prefs.analyticsQueueSize)
        .streamConfigFetchInterval(Duration.ofHours(12))
        .sendEventsInterval(Duration.ofSeconds(30))
        .isDebug(ReleaseUtil.isDevRelease)
        .build()
}
