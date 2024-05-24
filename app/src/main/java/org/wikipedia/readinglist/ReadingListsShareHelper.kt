package org.akhil.nitcwiki.readinglist

import android.content.Intent
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.analytics.eventplatform.ReadingListsAnalyticsHelper
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.json.JsonUtil
import org.akhil.nitcwiki.readinglist.database.ReadingList
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.GeoUtil
import org.akhil.nitcwiki.util.ReleaseUtil
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.log.L

object ReadingListsShareHelper {

    const val API_MAX_SIZE = 50
    const val PROVENANCE_PARAM = "rlsa1"

    fun shareEnabled(): Boolean {
        return ReleaseUtil.isPreBetaRelease ||
                (listOf("EG", "DZ", "MA", "KE", "CG", "AO", "GH", "NG", "IN", "BD", "PK", "LK", "NP").contains(GeoUtil.geoIPCountry.orEmpty()) &&
                        listOf("en", "ar", "hi", "fr", "bn", "es", "pt", "de", "ur", "arz", "si", "sw", "fa", "ne", "te").contains(WikipediaApp.instance.appOrSystemLanguageCode))
    }

    fun shareReadingList(activity: AppCompatActivity, readingList: ReadingList?) {
        if (readingList == null) {
            return
        }
        activity.lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
            L.e(throwable)
            FeedbackUtil.showError(activity, throwable)
        }) {
            val wikiPageTitlesMap = mutableMapOf<String, MutableList<String>>()

            readingList.pages.forEach {
                wikiPageTitlesMap.getOrPut(it.lang) { mutableListOf() }.add(it.apiTitle)
            }

            val wikiPageIdsMap = mutableMapOf<String, MutableMap<String, Int>>()

            wikiPageTitlesMap.keys.forEach { wikiLang ->
                wikiPageTitlesMap[wikiLang].orEmpty().chunked(API_MAX_SIZE).forEach { list ->
                    ServiceFactory.get(WikiSite.forLanguageCode(wikiLang)).getPageIds(list.joinToString("|")).query?.pages!!.forEach { page ->
                        wikiPageIdsMap.getOrPut(wikiLang) { mutableMapOf() }[StringUtil.addUnderscores(page.title)] = page.pageId
                    }
                }
            }

            val param = readingListToUrlParam(readingList, wikiPageIdsMap)
            val url = WikipediaApp.instance.wikiSite.url() + "/wiki/Special:ReadingLists?limport=$param&wprov=$PROVENANCE_PARAM"

            val finalUrl = if (Prefs.useUrlShortenerForSharing) ServiceFactory.get(WikipediaApp.instance.wikiSite).shortenUrl(url).shortenUrl?.shortUrl.orEmpty() else url

            ReadingListsAnalyticsHelper.logShareList(activity, readingList)

            val intent = Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_SUBJECT, readingList.title)
                    .putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.reading_list_share_message_v2) + " " + finalUrl)
                    .setType("text/plain")
            activity.startActivity(intent)

            ReadingListsShareSurveyHelper.activateSurvey()
        }
    }

    private fun readingListToUrlParam(readingList: ReadingList, pageIdMap: Map<String, Map<String, Int>>): String {
        val projectUrlMap = mutableMapOf<String, Collection<JsonPrimitive>>()
        pageIdMap.keys.forEach { key -> projectUrlMap[key] = pageIdMap[key]!!.values.map { JsonPrimitive(it) } }

        // TODO: for now we're not transmitting the free-form Name and Description of a reading list.
        val exportedReadingLists = ExportedReadingLists(projectUrlMap) // , readingList.title, readingList.description)
        return Base64.encodeToString(JsonUtil.encodeToString(exportedReadingLists)!!.toByteArray(), Base64.NO_WRAP)
    }

    @Suppress("unused")
    @Serializable
    class ExportedReadingLists(
        val list: Map<String, Collection<JsonPrimitive>>,
        val name: String? = null,
        val description: String? = null
    )
}
