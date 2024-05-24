package org.akhil.nitcwiki.feed.news

import android.net.Uri
import org.akhil.nitcwiki.dataclient.Service
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.ImageUrlUtil
import org.akhil.nitcwiki.util.ImageUrlUtil.getUrlForPreferredSize

class NewsLinkCard(private val page: PageSummary, private val wiki: WikiSite) : Card() {

    override fun title(): String {
        return page.displayTitle
    }

    override fun subtitle(): String? {
        return page.description
    }

    override fun image(): Uri? {
        val thumbUrl = page.thumbnailUrl
        return if (thumbUrl.isNullOrEmpty()) null else Uri.parse(ImageUrlUtil.getUrlForPreferredSize(thumbUrl, Service.PREFERRED_THUMB_SIZE))
    }

    override fun type(): CardType {
        return CardType.NEWS_ITEM_LINK
    }

    fun pageTitle(): PageTitle {
        return page.getPageTitle(wiki)
    }
}
