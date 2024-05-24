package org.akhil.nitcwiki.watchlist

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.activity.SingleFragmentActivity

class WatchlistActivity : SingleFragmentActivity<WatchlistFragment>() {
    public override fun createFragment(): WatchlistFragment {
        return WatchlistFragment.newInstance()
    }

    override fun onUnreadNotification() {
        fragment.updateNotificationDot(true)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WatchlistActivity::class.java)
        }
    }
}
