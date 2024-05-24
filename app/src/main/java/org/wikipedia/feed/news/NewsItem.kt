package org.akhil.nitcwiki.feed.news

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.util.ImageUrlUtil

@Parcelize
@Serializable
class NewsItem(
    val story: String = "",
    val links: List<PageSummary?> = emptyList()
) : Parcelable {

    fun linkCards(wiki: WikiSite): List<NewsLinkCard> {
        return links.filterNotNull().map { NewsLinkCard(it, wiki) }
    }

    fun thumb(): Uri? {
        return getFirstImageUri(links)?.let {
            Uri.parse(ImageUrlUtil.getUrlForPreferredSize(
                    it.toString(), Constants.PREFERRED_CARD_THUMBNAIL_SIZE))
        }
    }

    private fun getFirstImageUri(links: List<PageSummary?>): Uri? {
        return links.firstOrNull { !it?.thumbnailUrl.isNullOrEmpty() }?.run { Uri.parse(thumbnailUrl) }
    }
}
