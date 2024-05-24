package org.akhil.nitcwiki.dataclient.rollback

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.mwapi.MwResponse

@Serializable
class Rollback(
        val title: String?,
        @SerialName("pageid") val pageId: Int = 0,
        val summary: String? = null,
        @SerialName("revid") val revision: Long = 0,
        @SerialName("old_revid") val oldRevision: Long = 0,
        @SerialName("last_revid") val lastRevision: Long = 0
) : MwResponse()
