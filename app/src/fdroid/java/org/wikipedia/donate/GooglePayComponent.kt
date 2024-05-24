package org.akhil.nitcwiki.donate

import android.app.Activity
import android.content.Intent

object GooglePayComponent {
    suspend fun isGooglePayAvailable(activity: Activity): Boolean {
        return false
    }

    fun getDonateActivityIntent(activity: Activity, campaignId: String? = null, donateUrl: String? = null): Intent {
        return Intent()
    }
}
