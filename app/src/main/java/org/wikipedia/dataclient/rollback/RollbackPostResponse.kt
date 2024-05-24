package org.akhil.nitcwiki.dataclient.rollback

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.mwapi.MwPostResponse

@Serializable
class RollbackPostResponse : MwPostResponse() {
    val rollback: Rollback? = null
}
