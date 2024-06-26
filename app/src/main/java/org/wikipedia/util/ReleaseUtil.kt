package org.akhil.nitcwiki.util

import android.content.Context
import android.content.pm.PackageManager
import org.akhil.nitcwiki.BuildConfig
import org.akhil.nitcwiki.settings.Prefs

object ReleaseUtil {
    private const val RELEASE_PROD = 0
    private const val RELEASE_BETA = 1
    private const val RELEASE_ALPHA = 2
    private const val RELEASE_DEV = 3

    val isProdRelease: Boolean
        get() = calculateReleaseType() == RELEASE_PROD

    val isPreProdRelease: Boolean
        get() = calculateReleaseType() != RELEASE_PROD

    val isAlphaRelease: Boolean
        get() = calculateReleaseType() == RELEASE_ALPHA

    val isPreBetaRelease: Boolean
        get() = when (calculateReleaseType()) {
            RELEASE_PROD, RELEASE_BETA -> false
            else -> true
        }

    val isDevRelease: Boolean
        get() = calculateReleaseType() == RELEASE_DEV

    fun getChannel(ctx: Context): String {
        var channel = Prefs.appChannel
        if (channel == null) {
            channel = getChannelFromManifest(ctx)
            Prefs.appChannel = channel
        }
        return channel
    }

    private fun calculateReleaseType(): Int {
        return when {
            BuildConfig.APPLICATION_ID.contains("beta") -> RELEASE_BETA
            BuildConfig.APPLICATION_ID.contains("alpha") -> RELEASE_ALPHA
            BuildConfig.APPLICATION_ID.contains("dev") -> RELEASE_DEV
            else -> RELEASE_PROD
        }
    }

    private fun getChannelFromManifest(ctx: Context): String {
        return try {
            val info = ctx.packageManager
                    .getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA)
            val channel = info.metaData.getString(Prefs.appChannelKey)
            channel ?: ""
        } catch (t: Throwable) {
            ""
        }
    }
}
