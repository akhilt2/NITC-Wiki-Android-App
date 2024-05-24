package org.akhil.nitcwiki.dataclient.discussiontools

import kotlinx.serialization.Serializable

@Serializable
class DiscussionToolsSubscriptionList {
    val subscriptions: Map<String, Int> = emptyMap()
}
