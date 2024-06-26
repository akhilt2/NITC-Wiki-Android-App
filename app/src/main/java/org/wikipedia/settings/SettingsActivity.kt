package org.akhil.nitcwiki.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.json.JsonUtil

class SettingsActivity : SingleFragmentActivity<SettingsFragment>() {
    private lateinit var initialLanguageList: String
    private lateinit var initialFeedCardsEnabled: List<Boolean>
    private lateinit var initialFeedCardsOrder: List<Int>
    private val app = WikipediaApp.instance

    public override fun createFragment(): SettingsFragment {
        return SettingsFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialLanguageList = JsonUtil.encodeToString(app.languageState.appLanguageCodes).orEmpty()
        initialFeedCardsEnabled = Prefs.feedCardsEnabled
        initialFeedCardsOrder = Prefs.feedCardsOrder
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val finalLanguageList = JsonUtil.encodeToString(app.languageState.appLanguageCodes)
        if (requestCode == Constants.ACTIVITY_REQUEST_ADD_A_LANGUAGE &&
                finalLanguageList != initialLanguageList) {
            setResult(ACTIVITY_RESULT_LANGUAGE_CHANGED)
        } else if (requestCode == Constants.ACTIVITY_REQUEST_FEED_CONFIGURE &&
                (Prefs.feedCardsEnabled != initialFeedCardsEnabled || Prefs.feedCardsOrder != initialFeedCardsOrder)) {
            setResult(ACTIVITY_RESULT_FEED_CONFIGURATION_CHANGED)
        }
    }

    companion object {
        const val ACTIVITY_RESULT_LANGUAGE_CHANGED = 1
        const val ACTIVITY_RESULT_FEED_CONFIGURATION_CHANGED = 2
        const val ACTIVITY_RESULT_LOG_OUT = 3

        fun newIntent(ctx: Context): Intent {
            return Intent(ctx, SettingsActivity::class.java)
        }
    }
}
