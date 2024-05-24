package org.akhil.nitcwiki.feed.configure

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.feed.configure.ConfigureFragment.Companion.newInstance

class ConfigureActivity : SingleFragmentActivity<ConfigureFragment>() {
    override fun createFragment(): ConfigureFragment {
        return newInstance()
    }

    companion object {
        fun newIntent(context: Context, invokeSource: Int): Intent {
            return Intent(context, ConfigureActivity::class.java)
                .putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
        }
    }
}
