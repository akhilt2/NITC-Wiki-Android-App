package org.akhil.nitcwiki.feed.news

import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.UtcDate
import org.akhil.nitcwiki.feed.model.WikiSiteCard
import org.akhil.nitcwiki.util.L10nUtil
import java.util.concurrent.TimeUnit

class NewsCard(private val news: List<NewsItem>,
               private val age: Int,
               wiki: WikiSite) : WikiSiteCard(wiki) {

    override fun title(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_card_news_title)
    }

    override fun type(): CardType {
        return CardType.NEWS_LIST
    }

    override fun dismissHashCode(): Int {
        return TimeUnit.MILLISECONDS.toDays(date().baseCalendar.time.time).toInt() + wikiSite().hashCode()
    }

    fun date(): UtcDate {
        return UtcDate(age)
    }

    fun news(): List<NewsItem> {
        return news
    }
}
