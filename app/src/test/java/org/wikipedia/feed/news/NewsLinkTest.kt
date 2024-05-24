package org.akhil.nitcwiki.feed.news

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.json.JsonUtil
import org.akhil.nitcwiki.test.TestFileUtil

@RunWith(RobolectricTestRunner::class)
class NewsLinkTest {
    private lateinit var content: List<NewsItem>

    @Before
    @Throws(Throwable::class)
    fun setUp() {
        val json = TestFileUtil.readRawFile("news_2016_11_07.json")
        content = JsonUtil.decodeFromString(json)!!
    }

    @Test
    fun testTitleNormalization() {
        for (newsItem in content) {
            for (link in newsItem.links) {
                MatcherAssert.assertThat(
                    NewsLinkCard(link!!, TEST).title(),
                    Matchers.not(Matchers.containsString("_"))
                )
            }
        }
    }

    companion object {
        private val TEST = WikiSite.forLanguageCode("test")
    }
}
