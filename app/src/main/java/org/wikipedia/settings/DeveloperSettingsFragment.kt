package org.akhil.nitcwiki.settings

class DeveloperSettingsFragment : PreferenceLoaderFragment() {
    override fun loadPreferences() {
        DeveloperSettingsPreferenceLoader(this).loadPreferences()
    }

    companion object {
        fun newInstance(): DeveloperSettingsFragment {
            return DeveloperSettingsFragment()
        }
    }
}
