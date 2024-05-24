package org.akhil.nitcwiki.commons

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.extensions.parcelableExtra
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.ResourceUtil

class FilePageActivity : SingleFragmentActivity<FilePageFragment>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setImageZoomHelper()
        setStatusBarColor(ResourceUtil.getThemedColor(this, R.attr.paper_color))
        setNavigationBarColor(ResourceUtil.getThemedColor(this, R.attr.paper_color))
    }

    override fun createFragment(): FilePageFragment {
        return FilePageFragment.newInstance(intent.parcelableExtra(Constants.ARG_TITLE)!!,
                intent.getBooleanExtra(INTENT_EXTRA_ALLOW_EDIT, true))
    }

    companion object {
        const val INTENT_EXTRA_ALLOW_EDIT = "allowEdit"

        fun newIntent(context: Context, pageTitle: PageTitle, allowEdit: Boolean = true): Intent {
            return Intent(context, FilePageActivity::class.java)
                    .putExtra(Constants.ARG_TITLE, pageTitle)
                    .putExtra(INTENT_EXTRA_ALLOW_EDIT, allowEdit)
        }
    }
}
