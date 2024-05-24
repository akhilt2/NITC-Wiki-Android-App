package org.akhil.nitcwiki.analytics.metricsplatform

import org.akhil.nitcwiki.util.ActiveTimer

open class TimedMetricsEvent : MetricsEvent() {
    protected val timer = ActiveTimer()
}
