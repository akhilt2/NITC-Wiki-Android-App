package org.akhil.nitcwiki.analytics.eventplatform

import kotlinx.serialization.Serializable

@Serializable
class TestAppsEvent : MobileAppsEvent {
    constructor(_stream: String) : super(_stream)
}
