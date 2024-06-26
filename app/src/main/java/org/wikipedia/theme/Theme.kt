package org.akhil.nitcwiki.theme

import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.model.EnumCode

enum class Theme(val marshallingId: Int, val tag: String, @field:StyleRes @get:StyleRes
@param:StyleRes val resourceId: Int, @field:StringRes @get:StringRes @param:StringRes val nameId: Int) : EnumCode {

    LIGHT(0, "light", 0, R.string.color_theme_light),
    DARK(1, "dark", R.style.ThemeDark, R.string.color_theme_dark),
    BLACK(2, "black", R.style.ThemeBlack, R.string.color_theme_black),
    SEPIA(3, "sepia", R.style.ThemeSepia, R.string.color_theme_sepia);

    override fun code(): Int {
        return marshallingId
    }

    val isDefault: Boolean
        get() = this == fallback

    val isDark: Boolean
        get() = this == DARK || this == BLACK

    companion object {
        val fallback: Theme
            get() = LIGHT

        fun ofMarshallingId(id: Int): Theme? {
            return entries.find { it.marshallingId == id }
        }
    }
}
