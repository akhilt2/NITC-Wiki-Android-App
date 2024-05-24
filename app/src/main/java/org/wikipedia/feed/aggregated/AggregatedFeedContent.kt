package org.akhil.nitcwiki.feed.aggregated

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.image.FeaturedImage
import org.akhil.nitcwiki.feed.news.NewsItem
import org.akhil.nitcwiki.feed.onthisday.OnThisDay
import org.akhil.nitcwiki.feed.topread.TopRead

@Serializable
class AggregatedFeedContent(
    val tfa: PageSummary? = null,
    val news: List<NewsItem>? = null,
    @SerialName("mostread") val topRead: TopRead? = null,
    @SerialName("image") val potd: FeaturedImage? = null,
    val onthisday: List<OnThisDay.Event>? = null
)
