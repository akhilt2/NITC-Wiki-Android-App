package org.akhil.nitcwiki.categories

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.page.PageTitle

class CategoryActivityViewModel(bundle: Bundle) : ViewModel() {
    val pageTitle = bundle.parcelable<PageTitle>(Constants.ARG_TITLE)!!
    var showSubcategories = false

    val categoryMembersFlow = Pager(PagingConfig(pageSize = 10)) {
        CategoryMembersPagingSource(pageTitle, "page")
    }.flow.cachedIn(viewModelScope)

    val subcategoriesFlow = Pager(PagingConfig(pageSize = 10)) {
        CategoryMembersPagingSource(pageTitle, "subcat")
    }.flow.cachedIn(viewModelScope)

    class CategoryMembersPagingSource(
            val pageTitle: PageTitle,
            private val resultType: String
    ) : PagingSource<String, PageTitle>() {
        override suspend fun load(params: LoadParams<String>): LoadResult<String, PageTitle> {
            return try {
                val response = ServiceFactory.get(WikiSite.forLanguageCode(pageTitle.wikiSite.languageCode))
                        .getCategoryMembers(pageTitle.prefixedText, resultType, params.loadSize, params.key)
                if (response.query == null) {
                    return LoadResult.Page(emptyList(), null, null)
                }
                val titles = response.query!!.pages!!.map { page ->
                    PageTitle(page.title, pageTitle.wikiSite).also {
                        it.description = page.description.orEmpty()
                        it.thumbUrl = page.thumbUrl()
                        it.displayText = page.displayTitle(pageTitle.wikiSite.languageCode)
                    }
                }
                LoadResult.Page(titles, null, response.continuation?.gcmContinuation)
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<String, PageTitle>): String? {
            return null
        }
    }

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryActivityViewModel(bundle) as T
        }
    }
}
