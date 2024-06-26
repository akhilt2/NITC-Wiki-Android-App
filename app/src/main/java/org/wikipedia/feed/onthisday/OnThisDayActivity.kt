package org.akhil.nitcwiki.feed.onthisday

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelableExtra

class OnThisDayActivity : SingleFragmentActivity<OnThisDayFragment>() {

    override fun createFragment(): OnThisDayFragment {
        return OnThisDayFragment.newInstance(intent.getIntExtra(EXTRA_AGE, 0),
            intent.parcelableExtra(Constants.ARG_WIKISITE)!!,
            intent.getIntExtra(EXTRA_YEAR, -1),
            intent.getSerializableExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE) as InvokeSource)
    }

    companion object {
        const val EXTRA_AGE = "age"
        const val EXTRA_YEAR = "year"

        fun newIntent(context: Context, age: Int, year: Int,
                      wikiSite: WikiSite, invokeSource: InvokeSource): Intent {
            return Intent(context, OnThisDayActivity::class.java)
                .putExtra(EXTRA_AGE, age)
                .putExtra(Constants.ARG_WIKISITE, wikiSite)
                .putExtra(EXTRA_YEAR, year)
                .putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
        }
    }
}
