package org.akhil.nitcwiki.dataclient.watch

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.mwapi.MwPostResponse

@Serializable
class WatchPostResponse : MwPostResponse() {
    val batchcomplete: Boolean? = null

    val watch: List<Watch>? = null
        get() = field ?: emptyList()

    fun getFirst(): Watch? {
        return watch?.get(0)
    }
}
