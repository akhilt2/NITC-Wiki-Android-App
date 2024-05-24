package org.akhil.nitcwiki.places

import android.content.Context
import android.content.Intent
import android.location.Location
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.extensions.parcelableExtra
import org.akhil.nitcwiki.page.PageTitle

class PlacesActivity : SingleFragmentActivity<PlacesFragment>() {
    public override fun createFragment(): PlacesFragment {
        return PlacesFragment.newInstance(intent.parcelableExtra(Constants.ARG_TITLE), intent.parcelableExtra(EXTRA_LOCATION))
    }

    companion object {
        const val EXTRA_LOCATION = "location"
        fun newIntent(context: Context, pageTitle: PageTitle? = null, location: Location? = null): Intent {
            return Intent(context, PlacesActivity::class.java)
                .putExtra(Constants.ARG_TITLE, pageTitle)
                .putExtra(EXTRA_LOCATION, location)
        }
    }
}
