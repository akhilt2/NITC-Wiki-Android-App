package org.akhil.nitcwiki.commons

import org.akhil.nitcwiki.dataclient.mwapi.MwQueryPage

class FilePage(
    val thumbnailWidth: Int = 0,
    val thumbnailHeight: Int = 0,
    val imageFromCommons: Boolean = false,
    val showEditButton: Boolean = false,
    val showFilename: Boolean = false,
    val page: MwQueryPage = MwQueryPage(),
    val imageTags: Map<String, List<String>> = emptyMap()
)
