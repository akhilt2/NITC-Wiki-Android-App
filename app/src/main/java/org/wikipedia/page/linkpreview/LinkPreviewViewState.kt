package org.akhil.nitcwiki.page.linkpreview

import org.akhil.nitcwiki.dataclient.mwapi.MwQueryPage
import org.akhil.nitcwiki.dataclient.page.PageSummary

sealed class LinkPreviewViewState {
    data object Loading : LinkPreviewViewState()
    data object Completed : LinkPreviewViewState()
    data class Error(val throwable: Throwable) : LinkPreviewViewState()
    data class Content(val data: PageSummary) : LinkPreviewViewState()
    data class Gallery(val data: List<MwQueryPage>) : LinkPreviewViewState()
    data class Watch(val isWatch: Boolean) : LinkPreviewViewState()
}
