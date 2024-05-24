package org.akhil.nitcwiki.settings.languages

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.settings.languages.WikipediaLanguagesFragment.Companion.newInstance
import org.akhil.nitcwiki.widgets.WidgetProviderFeaturedPage.Companion.forceUpdateWidget

class WikipediaLanguagesActivity : SingleFragmentActivity<WikipediaLanguagesFragment>() {
    override fun createFragment(): WikipediaLanguagesFragment {
        return newInstance(intent.getSerializableExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE) as InvokeSource)
    }

    override fun onDestroy() {
        // Regardless of why the activity is closing, let's explicitly refresh any
        // language-dependent widgets.
        forceUpdateWidget(applicationContext)
        super.onDestroy()
    }

    companion object {
        fun newIntent(context: Context, invokeSource: InvokeSource): Intent {
            return Intent(context, WikipediaLanguagesActivity::class.java)
                    .putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
        }
    }
}
