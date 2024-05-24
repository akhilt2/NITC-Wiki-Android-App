package org.akhil.nitcwiki.feed.becauseyouread

import android.net.Uri
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.ListCard
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.L10nUtil

class BecauseYouReadCard(val pageTitle: PageTitle,
                         itemCards: List<BecauseYouReadItemCard>) : ListCard<BecauseYouReadItemCard>(itemCards, pageTitle.wikiSite) {

    override fun title(): String {
        return L10nUtil.getStringForArticleLanguage(pageTitle, R.string.view_because_you_read_card_title)
    }

    override fun image(): Uri? {
        return if (pageTitle.thumbUrl.isNullOrEmpty()) null else Uri.parse(pageTitle.thumbUrl)
    }

    override fun extract(): String {
        return pageTitle.description.orEmpty()
    }

    override fun type(): CardType {
        return CardType.BECAUSE_YOU_READ_LIST
    }

    fun pageTitle(): String {
        return pageTitle.displayText
    }

    override fun dismissHashCode(): Int {
        return pageTitle.hashCode()
    }
}
