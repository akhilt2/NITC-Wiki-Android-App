
package org.akhil.nitcwiki.readinglist

import android.content.Context
import android.util.Base64
import kotlinx.serialization.json.int
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.dataclient.Service
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.mwapi.MwQueryPage
import org.akhil.nitcwiki.json.JsonUtil
import org.akhil.nitcwiki.readinglist.database.ReadingList
import org.akhil.nitcwiki.readinglist.database.ReadingListPage
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.ImageUrlUtil
import org.akhil.nitcwiki.util.StringUtil
import java.util.*

object ReadingListsReceiveHelper {

    suspend fun receiveReadingLists(context: Context, encodedJson: String): ReadingList {
        val readingListData = getExportedReadingLists(encodedJson)
        val listTitle = readingListData?.name.orEmpty().ifEmpty { context.getString(R.string.reading_lists_preview_header_title) }
        val listDescription = readingListData?.description.orEmpty().ifEmpty { DateUtil.getTimeAndDateString(context, Date()) }
        val listPages = mutableListOf<ReadingListPage>()

        // Request API by languages
        readingListData?.list?.forEach { map ->
            val wikiSite = WikiSite.forLanguageCode(map.key)
            map.value.chunked(ReadingListsShareHelper.API_MAX_SIZE).forEach { list ->
                val listOfTitles = list.filter { it.isString }.map { it.content }
                val listOfIds = list.filter { !it.isString }.map { it.int }

                val pages = mutableListOf<MwQueryPage>()
                if (listOfIds.isNotEmpty()) {
                    pages.addAll(ServiceFactory.get(wikiSite).getInfoByPageIdsOrTitles(pageIds = listOfIds.joinToString(separator = "|"))
                        .query?.pages.orEmpty())
                }
                if (listOfTitles.isNotEmpty()) {
                    pages.addAll(ServiceFactory.get(wikiSite).getInfoByPageIdsOrTitles(titles = listOfTitles.joinToString(separator = "|"))
                        .query?.pages.orEmpty())
                }
                pages.forEach {
                    val readingListPage = ReadingListPage(
                        wikiSite,
                        it.namespace(),
                        it.displayTitle(wikiSite.languageCode),
                        StringUtil.addUnderscores(it.title),
                        it.description,
                        ImageUrlUtil.getUrlForPreferredSize(it.thumbUrl().orEmpty(), Service.PREFERRED_THUMB_SIZE),
                        lang = wikiSite.languageCode
                    )
                    listPages.add(readingListPage)
                }
            }
        }

        listPages.sortBy { it.apiTitle }

        val readingList = ReadingList(listTitle, listDescription)
        readingList.pages.addAll(listPages)

        return readingList
    }

    private fun getExportedReadingLists(encodedJson: String): ReadingListsShareHelper.ExportedReadingLists? {
        return JsonUtil.decodeFromString(String(Base64.decode(encodedJson, Base64.NO_WRAP)))
    }
}
