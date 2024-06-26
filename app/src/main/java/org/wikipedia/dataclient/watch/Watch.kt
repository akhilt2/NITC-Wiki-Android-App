package org.akhil.nitcwiki.dataclient.watch

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.mwapi.MwResponse

@Serializable
class Watch(val title: String?,
            val ns: Int = 0,
            val pageid: Int = 0,
            val expiry: String? = null,
            val watched: Boolean = false,
            val unwatched: Boolean = false,
            val missing: Boolean = false) : MwResponse()
