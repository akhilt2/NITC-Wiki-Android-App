package org.akhil.nitcwiki.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import org.akhil.nitcwiki.R

object CustomTabsUtil {

    fun openInCustomTab(context: Context, url: String) {
        val colors = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ResourceUtil.getThemedColor(context, R.attr.paper_color))
                .setNavigationBarColor(ResourceUtil.getThemedColor(context, R.attr.paper_color))
                .setSecondaryToolbarColor(ResourceUtil.getThemedColor(context, R.attr.paper_color))
                .setNavigationBarDividerColor(ResourceUtil.getThemedColor(context, R.attr.secondary_color))
                .build()
        CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colors)
                .build()
                .launchUrl(context, Uri.parse(url))
    }
}
