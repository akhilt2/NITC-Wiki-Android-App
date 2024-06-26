package org.akhil.nitcwiki.util

import android.content.Context
import android.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import org.akhil.nitcwiki.settings.Prefs

object TransitionUtil {
    fun getSharedElements(context: Context, vararg views: View): Array<Pair<View, String>> {
        return views.filter {
            (it is TextView && it.text.isNotEmpty()) ||
                    (it is ImageView && it.isVisible && (it.parent as View).isVisible &&
                            !DimenUtil.isLandscape(context) && Prefs.isImageDownloadEnabled)
        }
                .map { Pair(it, it.transitionName) }
                .toTypedArray()
    }
}
