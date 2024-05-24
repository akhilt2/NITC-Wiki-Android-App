package org.akhil.nitcwiki.feed.random

import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.feed.featured.FeaturedArticleCard
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.util.L10nUtil

class RandomCard(page: PageSummary, age: Int, wiki: WikiSite) : FeaturedArticleCard(page, age, wiki) {

    override fun title(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_random_article_card_title)
    }

    override fun footerActionText(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_random_article_card_action)
    }

    override fun type(): CardType {
        return CardType.RANDOM
    }

    override fun historyEntrySource(): Int {
        return HistoryEntry.SOURCE_FEED_RANDOM
    }
}
