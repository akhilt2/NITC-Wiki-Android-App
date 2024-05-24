package org.akhil.nitcwiki.feed.image

import kotlinx.serialization.Serializable
import org.akhil.nitcwiki.gallery.GalleryItem
import org.akhil.nitcwiki.gallery.ImageInfo
import org.akhil.nitcwiki.util.StringUtil

@Serializable
class FeaturedImage : GalleryItem() {

    val title = ""

    val image = ImageInfo()

    init {
        titles = Titles(title, StringUtil.addUnderscores(title), title)
        original.source = image.source
    }
}
