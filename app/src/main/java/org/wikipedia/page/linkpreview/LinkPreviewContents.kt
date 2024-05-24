package org.akhil.nitcwiki.page.linkpreview

import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.util.L10nUtil

class LinkPreviewContents(pageSummary: PageSummary, wiki: WikiSite) {
    val title = pageSummary.getPageTitle(wiki)
    val ns = pageSummary.namespace
    val isDisambiguation = pageSummary.type == PageSummary.TYPE_DISAMBIGUATION
    val extract = if (isDisambiguation)
        "<p>" + L10nUtil.getStringForArticleLanguage(title, R.string.link_preview_disambiguation_description) + "</p>" + pageSummary.extractHtml
        else pageSummary.extractHtml
}
