package org.akhil.nitcwiki.analytics.eventplatform

import kotlinx.serialization.Serializable

@Serializable
class TestEvent : Event {
    constructor(_stream: String) : super(_stream)
}
