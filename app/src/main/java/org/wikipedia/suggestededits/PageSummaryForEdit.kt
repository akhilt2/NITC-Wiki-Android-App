package org.akhil.nitcwiki.suggestededits

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.gallery.ExtMetadata
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.ImageUrlUtil

@Parcelize
data class PageSummaryForEdit(
        var title: String,
        var lang: String,
        var pageTitle: PageTitle,
        var displayTitle: String = pageTitle.displayText,
        var description: String?,
        var thumbnailUrl: String?,
        var extract: String? = null,
        var extractHtml: String? = null,
        var timestamp: String? = null,
        var user: String? = null,
        var metadata: ExtMetadata? = null
) : Parcelable {
    fun getPreferredSizeThumbnailUrl(): String = ImageUrlUtil.getUrlForPreferredSize(thumbnailUrl!!, Constants.PREFERRED_CARD_THUMBNAIL_SIZE)
}
