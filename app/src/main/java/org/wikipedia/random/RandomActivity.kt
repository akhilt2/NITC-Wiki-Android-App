package org.akhil.nitcwiki.random

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelableExtra

class RandomActivity : SingleFragmentActivity<RandomFragment>() {

    companion object {
        fun newIntent(context: Context, wikiSite: WikiSite, invokeSource: InvokeSource?): Intent {
            return Intent(context, RandomActivity::class.java).apply {
                putExtra(Constants.ARG_WIKISITE, wikiSite)
                putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.elevation = 0f
    }

    public override fun createFragment(): RandomFragment {
        return RandomFragment.newInstance(intent.parcelableExtra(Constants.ARG_WIKISITE)!!,
                intent.getSerializableExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE) as InvokeSource)
    }
}
