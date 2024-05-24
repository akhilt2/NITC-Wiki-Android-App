package org.akhil.nitcwiki.feed.featured

import android.net.Uri
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.WikiSiteCard
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.L10nUtil

open class FeaturedArticleCard(private val page: PageSummary,
                               private val age: Int, wiki: WikiSite) : WikiSiteCard(wiki) {

    override fun title(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_featured_article_card_title)
    }

    override fun subtitle(): String {
        return DateUtil.getFeedCardDateString(age)
    }

    override fun image(): Uri? {
        return if (page.thumbnailUrl.isNullOrEmpty()) null else Uri.parse(page.thumbnailUrl)
    }

    override fun extract(): String? {
        return page.extract
    }

    override fun type(): CardType {
        return CardType.FEATURED_ARTICLE
    }

    override fun dismissHashCode(): Int {
        return page.apiTitle.hashCode()
    }

    open fun historyEntrySource(): Int {
        return HistoryEntry.SOURCE_FEED_FEATURED
    }

    open fun footerActionText(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_main_page_card_title)
    }

    fun articleTitle(): String {
        return page.displayTitle
    }

    fun articleSubtitle(): String? {
        return page.description
    }

    fun historyEntry(): HistoryEntry {
        return HistoryEntry(page.getPageTitle(wikiSite()), historyEntrySource())
    }
}
