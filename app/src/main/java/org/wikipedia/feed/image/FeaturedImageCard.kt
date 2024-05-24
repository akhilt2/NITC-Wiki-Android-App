package org.akhil.nitcwiki.feed.image

import android.net.Uri
import androidx.core.net.toUri
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.WikiSiteCard
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.L10nUtil

class FeaturedImageCard(private val featuredImage: FeaturedImage,
                        private val age: Int,
                        wiki: WikiSite) : WikiSiteCard(wiki) {

    override fun title(): String {
        return L10nUtil.getStringForArticleLanguage(wikiSite().languageCode, R.string.view_featured_image_card_title)
    }

    override fun subtitle(): String {
        return DateUtil.getFeedCardDateString(age)
    }

    override fun image(): Uri? {
        return featuredImage.thumbnailUrl.ifEmpty { null }?.toUri()
    }

    override fun type(): CardType {
        return CardType.FEATURED_IMAGE
    }

    override fun dismissHashCode(): Int {
        return featuredImage.title.hashCode()
    }

    fun baseImage(): FeaturedImage {
        return featuredImage
    }

    fun age(): Int {
        return age
    }

    fun filename(): String {
        return featuredImage.title
    }

    fun description(): String {
        return featuredImage.description.text
    }
}
