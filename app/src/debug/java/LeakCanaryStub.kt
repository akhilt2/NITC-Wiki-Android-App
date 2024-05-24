package org.akhil.nitcwiki

import leakcanary.LeakCanary
import org.akhil.nitcwiki.settings.Prefs

fun setupLeakCanary() {
    val enabled = Prefs.isMemoryLeakTestEnabled
    LeakCanary.config = LeakCanary.config.copy(dumpHeap = enabled)
}
