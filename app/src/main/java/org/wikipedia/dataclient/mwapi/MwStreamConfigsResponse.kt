package org.akhil.nitcwiki.dataclient.mwapi

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.analytics.eventplatform.StreamConfig

@Serializable
class MwStreamConfigsResponse : MwResponse() {

    private val streams: Map<String, StreamConfig>? = null
    val streamConfigs: Map<String, StreamConfig>
        get() = streams ?: emptyMap()
}
