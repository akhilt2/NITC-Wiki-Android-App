package org.akhil.nitcwiki.settings

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.json.JsonUtil
import org.akhil.nitcwiki.util.log.L

object RemoteConfig {
    private var curConfig: RemoteConfigImpl? = null

    // If there's no pref set, just give back the empty JSON Object
    val config: RemoteConfigImpl
        get() {
            if (curConfig == null) {
                curConfig = try {
                    JsonUtil.decodeFromString<RemoteConfigImpl>(Prefs.remoteConfigJson)
                } catch (e: Exception) {
                    L.e(e)
                    RemoteConfigImpl()
                }
            }
            return curConfig!!
        }

    fun updateConfig(configStr: String) {
        Prefs.remoteConfigJson = configStr
        curConfig = null
    }

    @Suppress("unused")
    @Serializable
    class RemoteConfigImpl {
        val disableReadingListSync = false
        val disableAnonEditing = false
    }
}
