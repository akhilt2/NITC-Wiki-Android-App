package org.akhil.nitcwiki.suggestededits

class ImageTag(
        val wikidataId: String,
        var label: String,
        var description: String? = null,
        var isSelected: Boolean = false
)
