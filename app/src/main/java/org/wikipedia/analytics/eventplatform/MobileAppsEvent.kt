package org.akhil.nitcwiki.analytics.eventplatform

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.auth.AccountUtil

@Suppress("unused")
@Serializable
sealed class MobileAppsEvent(@Transient private val _streamName: String = "") : Event(_streamName) {

    @SerialName("is_anon") @Required private val anon = !AccountUtil.isLoggedIn
    @SerialName("app_session_id") @Required private val sessionId = EventPlatformClient.AssociationController.sessionId
    @SerialName("app_install_id") @Required private val appInstallId = WikipediaApp.instance.appInstallID
}
