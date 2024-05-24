package org.akhil.nitcwiki.dataclient.wikidata

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.mwapi.MwPostResponse

@Serializable
class EntityPostResponse : MwPostResponse() {
    val entity: Entities.Entity? = null
}
