package org.akhil.nitcwiki.feed.topread

import android.net.Uri
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType

class TopReadItemCard internal constructor(private val page: PageSummary,
                                           private val wiki: WikiSite) : Card() {

    override fun title(): String {
        return page.displayTitle
    }

    override fun subtitle(): String? {
        return page.description
    }

    override fun image(): Uri? {
        return if (page.thumbnailUrl.isNullOrEmpty()) null else Uri.parse(page.thumbnailUrl)
    }

    override fun type(): CardType {
        return CardType.MOST_READ_ITEM
    }

    val pageViews get() = page.views
    val viewHistory get() = page.viewHistory ?: emptyList()
    val pageTitle get() = page.getPageTitle(wiki)
}
