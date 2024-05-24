package org.akhil.nitcwiki.descriptions

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.activity.SingleFragmentActivityTransparent

class DescriptionEditSuccessActivity : SingleFragmentActivityTransparent<DescriptionEditSuccessFragment>(), DescriptionEditSuccessFragment.Callback {
    override fun createFragment(): DescriptionEditSuccessFragment {
        return DescriptionEditSuccessFragment.newInstance()
    }

    override fun onDismissClick() {
        setResult(RESULT_OK_FROM_EDIT_SUCCESS, intent)
        finish()
    }

    companion object {
        const val RESULT_OK_FROM_EDIT_SUCCESS = 1

        fun newIntent(context: Context, invokeSource: InvokeSource): Intent {
            return Intent(context, DescriptionEditSuccessActivity::class.java)
                    .putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
        }
    }
}
