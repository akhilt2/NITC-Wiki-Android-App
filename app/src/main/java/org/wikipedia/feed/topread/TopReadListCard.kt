package org.akhil.nitcwiki.feed.topread

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.ListCard
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.L10nUtil

@Parcelize
class TopReadListCard(private val articles: TopRead, val site: WikiSite) :
    ListCard<TopReadItemCard>(toItems(articles.articles, site), site), Parcelable {

    override fun title(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_top_read_card_title)
    }

    override fun subtitle(): String {
        return DateUtil.getShortDateString(articles.localDate)
    }

    override fun type(): CardType {
        return CardType.TOP_READ_LIST
    }

    fun footerActionText(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_top_read_card_action)
    }

    override fun dismissHashCode(): Int {
        return articles.localDate.toEpochDay().toInt() + wikiSite().hashCode()
    }

    companion object {
        fun toItems(articles: List<PageSummary>, wiki: WikiSite): List<TopReadItemCard> {
            return articles.map { TopReadItemCard(it, wiki) }
        }
    }
}
