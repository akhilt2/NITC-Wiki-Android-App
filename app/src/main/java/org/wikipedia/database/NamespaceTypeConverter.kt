package org.akhil.nitcwiki.database

import androidx.room.TypeConverter
import org.akhil.nitcwiki.page.Namespace

class NamespaceTypeConverter {
    @TypeConverter
    fun intToNamespace(value: Int?): Namespace? {
        return if (value == null) null else Namespace.of(value)
    }

    @TypeConverter
    fun namespaceToInt(ns: Namespace?): Int? {
        return ns?.code()
    }
}
