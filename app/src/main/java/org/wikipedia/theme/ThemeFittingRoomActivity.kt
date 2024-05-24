package org.akhil.nitcwiki.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.page.ExclusiveBottomSheetPresenter

class ThemeFittingRoomActivity : SingleFragmentActivity<ThemeFittingRoomFragment>(), ThemeChooserDialog.Callback {
    private var themeChooserDialog: ThemeChooserDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            themeChooserDialog = ThemeChooserDialog.newInstance(Constants.InvokeSource.SETTINGS)
            ExclusiveBottomSheetPresenter.show(supportFragmentManager, themeChooserDialog!!)
        }

        // Don't let changed theme affect the status bar color and navigation bar color
        setStatusBarColor(ContextCompat.getColor(this, android.R.color.black))
        setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black))
    }

    override fun createFragment(): ThemeFittingRoomFragment {
        return ThemeFittingRoomFragment.newInstance()
    }

    override fun onToggleDimImages() {
        ActivityCompat.recreate(this)
    }

    override fun onToggleReadingFocusMode() {
    }

    override fun onCancelThemeChooser() {
        finish()
    }

    override fun onEditingPrefsChanged() { }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ThemeFittingRoomActivity::class.java)
        }
    }
}
