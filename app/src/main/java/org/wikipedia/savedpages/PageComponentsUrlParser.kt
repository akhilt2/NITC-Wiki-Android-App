package org.akhil.nitcwiki.savedpages

import org.jsoup.Jsoup
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.util.UriUtil.resolveProtocolRelativeUrl
import org.akhil.nitcwiki.util.log.L

object PageComponentsUrlParser {
    fun parse(html: String, site: WikiSite): List<String> {
        return try {
            val document = Jsoup.parse(html)
            val css = document.select("link[rel=stylesheet]")
            val javascript = document.select("script")

            // parsing CSS styles and JavaScript files
            listOf(css.map { it.attr("href") }, javascript.map { it.attr("src") })
                .flatten()
                .filter { it.isNotEmpty() }
                .map { resolveProtocolRelativeUrl(site, it) }
        } catch (e: Exception) {
            L.d("Parsing exception$e")
            emptyList()
        }
    }
}
