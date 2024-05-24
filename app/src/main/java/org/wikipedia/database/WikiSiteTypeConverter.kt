package org.akhil.nitcwiki.database

import androidx.room.TypeConverter
import org.akhil.nitcwiki.dataclient.WikiSite

class WikiSiteTypeConverter {
    @TypeConverter
    fun stringToWikiSite(value: String?): WikiSite? {
        return if (value == null) null else WikiSite(value)
    }

    @TypeConverter
    fun wikiSiteToString(wikiSite: WikiSite?): String? {
        return wikiSite?.authority()
    }
}
