package org.akhil.nitcwiki.page

import android.location.Location
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.dataclient.page.Protection
import org.akhil.nitcwiki.parcel.DateParceler
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ImageUrlUtil
import org.akhil.nitcwiki.util.UriUtil
import java.util.Date

@Parcelize
@TypeParceler<Date, DateParceler>()
data class PageProperties constructor(
    val pageId: Int = 0,
    val namespace: Namespace,
    val revisionId: Long = 0,
    val lastModified: Date = Date(),
    val displayTitle: String = "",
    private var editProtectionStatus: String = "",
    val isMainPage: Boolean = false,
    /** Nullable URL with no scheme. For example, foo.bar.com/ instead of http://foo.bar.com/.  */
    val leadImageUrl: String? = null,
    val leadImageName: String? = null,
    val leadImageWidth: Int = 0,
    val leadImageHeight: Int = 0,
    val geo: Location? = null,
    val wikiBaseItem: String? = null,
    val descriptionSource: String? = null,
    // FIXME: This is not a true page property, since it depends on current user.
    var canEdit: Boolean = false
) : Parcelable {

    @IgnoredOnParcel
    var protection: Protection? = null
        set(value) {
            field = value
            editProtectionStatus = value?.firstAllowedEditorRole.orEmpty()
            canEdit = editProtectionStatus.isEmpty() || isLoggedInUserAllowedToEdit
        }

    /**
     * Side note: Should later be moved out of this class but I like the similarities with
     * PageProperties(JSONObject).
     */
    constructor(pageSummary: PageSummary) : this(
        pageSummary.pageId,
        pageSummary.ns,
        pageSummary.revision,
        if (pageSummary.timestamp.isEmpty()) Date() else DateUtil.iso8601DateParse(pageSummary.timestamp),
        pageSummary.displayTitle,
        isMainPage = pageSummary.type == PageSummary.TYPE_MAIN_PAGE,
        leadImageUrl = pageSummary.thumbnailUrl?.let { ImageUrlUtil.getUrlForPreferredSize(it, DimenUtil.calculateLeadImageWidth()) },
        leadImageName = UriUtil.decodeURL(pageSummary.leadImageName.orEmpty()),
        leadImageWidth = pageSummary.thumbnailWidth,
        leadImageHeight = pageSummary.thumbnailHeight,
        geo = pageSummary.coordinates,
        wikiBaseItem = pageSummary.wikiBaseItem,
        descriptionSource = pageSummary.descriptionSource
    )

    constructor(title: PageTitle, isMainPage: Boolean) : this(namespace = title.namespace(),
        displayTitle = title.displayText, isMainPage = isMainPage)

    private val isLoggedInUserAllowedToEdit: Boolean
        get() = protection?.run { AccountUtil.isMemberOf(editRoles) } ?: false
}
