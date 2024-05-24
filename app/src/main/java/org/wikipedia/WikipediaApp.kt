package org.akhil.nitcwiki

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.Window
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.akhil.nitcwiki.analytics.InstallReferrerListener
import org.akhil.nitcwiki.analytics.eventplatform.AppSessionEvent
import org.akhil.nitcwiki.analytics.eventplatform.EventPlatformClient
import org.akhil.nitcwiki.appshortcuts.AppShortcuts
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.concurrency.RxBus
import org.akhil.nitcwiki.connectivity.ConnectionStateMonitor
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.SharedPreferenceCookieManager
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.events.ChangeTextSizeEvent
import org.akhil.nitcwiki.events.ThemeFontChangeEvent
import org.akhil.nitcwiki.language.AcceptLanguageUtil
import org.akhil.nitcwiki.language.AppLanguageState
import org.akhil.nitcwiki.notifications.NotificationCategory
import org.akhil.nitcwiki.notifications.NotificationPollBroadcastReceiver
import org.akhil.nitcwiki.page.tabs.Tab
import org.akhil.nitcwiki.push.WikipediaFirebaseMessagingService
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.theme.Theme
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ReleaseUtil
import org.akhil.nitcwiki.util.log.L
import java.util.*

class WikipediaApp : Application() {
    init {
        instance = this
    }

    val mainThreadHandler by lazy { Handler(mainLooper) }
    val languageState by lazy { AppLanguageState(this) }
    val appSessionEvent by lazy { AppSessionEvent() }

    val userAgent by lazy {
        var channel = ReleaseUtil.getChannel(this)
        channel = if (channel.isBlank()) "" else " $channel"
        String.format("WikipediaApp/%s (Android %s; %s; %s Build/%s)%s",
            BuildConfig.VERSION_NAME,
            Build.VERSION.RELEASE,
            getString(R.string.device_type),
            Build.MODEL,
            Build.ID,
            channel
        )
    }

    private val activityLifecycleHandler = ActivityLifecycleHandler()
    private var defaultWikiSite: WikiSite? = null

    val connectionStateMonitor = ConnectionStateMonitor()
    val bus = RxBus()
    val tabList = mutableListOf<Tab>()

    var currentTheme = Theme.fallback
        set(value) {
            if (value !== field) {
                field = value
                Prefs.currentThemeId = currentTheme.marshallingId
                bus.post(ThemeFontChangeEvent())
            }
        }

    val appOrSystemLanguageCode: String
        get() = languageState.appLanguageCode

    val versionCode: Int
        get() {
            // When we had ABI-specific version codes, they were structured in increments of 10000,
            // so just take the actual version code modulo the increment.
            return BuildConfig.VERSION_CODE % 10000
        }

    val appInstallID: String
        get() {
            var id = Prefs.appInstallId
            if (id == null) {
                id = UUID.randomUUID().toString()
                Prefs.appInstallId = id
            }
            return id
        }

    /**
     * Default "home" wiki for the app
     * Use PageTitle.getWikiSite() to get the article wiki
     */
    @get:Synchronized
    val wikiSite: WikiSite
        get() {
            // TODO: why don't we ensure that the app language hasn't changed here instead of the client?
            if (defaultWikiSite == null) {
                val lang = if (Prefs.mediaWikiBaseUriSupportsLangCode) appOrSystemLanguageCode else ""
                defaultWikiSite = WikiSite.forLanguageCode(lang)
            }
            return defaultWikiSite!!
        }

    // handle the case where we have a single tab with an empty backstack, which shouldn't count as a valid tab:
    val tabCount
        get() = if (tabList.size > 1) tabList.size else if (tabList.isEmpty()) 0 else if (tabList[0].backStack.isEmpty()) 0 else tabList.size

    val isOnline
        get() = connectionStateMonitor.isOnline()

    val haveMainActivity
        get() = activityLifecycleHandler.haveMainActivity()

    val isAnyActivityResumed
        get() = activityLifecycleHandler.isAnyActivityResumed

    val voiceRecognitionAvailable by lazy {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        } catch (e: Exception) {
            L.e(e)
            false
        }
    }

    override fun onCreate() {
        super.onCreate()

        WikiSite.setDefaultBaseUrl(Prefs.mediaWikiBaseUrl)

        connectionStateMonitor.enable()

        setupLeakCanary()

        // See Javadocs and http://developer.android.com/tools/support-library/index.html#rev23-4-0
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // This handler will catch exceptions thrown from Observables after they are disposed,
        // or from Observables that are (deliberately or not) missing an onError handler.
        // TODO: consider more comprehensive handling of these errors.
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())

        currentTheme = unmarshalTheme(Prefs.currentThemeId)

        initTabs()
        enableWebViewDebugging()
        registerActivityLifecycleCallbacks(activityLifecycleHandler)
        registerComponentCallbacks(activityLifecycleHandler)
        NotificationCategory.createNotificationChannels(this)
        AppShortcuts.setShortcuts(this)

        // Kick the notification receiver, in case it hasn't yet been started by the system.
        NotificationPollBroadcastReceiver.startPollTask(this)
        InstallReferrerListener.newInstance(this)

        // For good measure, explicitly call our token subscription function, in case the
        // API failed in previous attempts.
        WikipediaFirebaseMessagingService.updateSubscription()

        EventPlatformClient.setUpStreamConfigs()
    }

    /**
     * @return the value that should go in the Accept-Language header.
     */
    fun getAcceptLanguage(wiki: WikiSite?): String {
        val wikiLang = if (wiki == null || "meta" == wiki.languageCode) "" else wiki.languageCode
        return AcceptLanguageUtil.getAcceptLanguage(
            languageState.getBcp47LanguageCode(wikiLang),
            languageState.getBcp47LanguageCode(languageState.appLanguageCode),
                languageState.getBcp47LanguageCode(languageState.systemLanguageCode))
    }

    fun constrainFontSizeMultiplier(mult: Int): Int {
        return mult.coerceIn(resources.getInteger(R.integer.minTextSizeMultiplier),
                resources.getInteger(R.integer.maxTextSizeMultiplier))
    }

    fun setFontSizeMultiplier(mult: Int): Boolean {
        val multiplier = constrainFontSizeMultiplier(mult)
        if (multiplier != Prefs.textSizeMultiplier) {
            Prefs.textSizeMultiplier = multiplier
            bus.post(ChangeTextSizeEvent())
            return true
        }
        return false
    }

    fun setFontFamily(fontFamily: String) {
        if (fontFamily != Prefs.fontFamily) {
            Prefs.fontFamily = fontFamily
            bus.post(ThemeFontChangeEvent())
        }
    }

    fun putCrashReportProperty(key: String?, value: String?) {
        // TODO: add custom properties to crash report
    }

    fun logCrashManually(throwable: Throwable) {
        L.e(throwable)
        // TODO: send exception to custom crash reporting system
    }

    fun commitTabState() {
        if (tabList.isEmpty()) {
            Prefs.clearTabs()
            initTabs()
        } else {
            Prefs.tabs = tabList
        }
    }

    /**
     * Gets the current size of the app's font. This is given as a device-specific size (not "sp"),
     * and can be passed directly to setTextSize() functions.
     * @param window The window on which the font will be displayed.
     * @return Actual current size of the font.
     */
    fun getFontSize(window: Window, editing: Boolean = false): Float {
        return DimenUtil.getFontSizeFromSp(window,
                resources.getDimension(R.dimen.textSize)) * (1.0f + (if (editing) Prefs.editingTextSizeMultiplier else Prefs.textSizeMultiplier) *
                DimenUtil.getFloat(R.dimen.textSizeMultiplierFactor))
    }

    @Synchronized
    fun resetWikiSite() {
        defaultWikiSite = null
    }

    @SuppressLint("CheckResult")
    fun logOut() {
        L.d("Logging out")
        AccountUtil.removeAccount()
        Prefs.isPushNotificationTokenSubscribed = false
        Prefs.pushNotificationTokenOld = ""
        ServiceFactory.get(wikiSite).getTokenObservable()
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val csrfToken = it.query!!.csrfToken()
                    WikipediaFirebaseMessagingService.unsubscribePushToken(csrfToken!!, Prefs.pushNotificationToken)
                            .flatMap { ServiceFactory.get(wikiSite).postLogout(csrfToken).subscribeOn(Schedulers.io()) }
                }
                .doFinally { SharedPreferenceCookieManager.instance.clearAllCookies() }
                .subscribe({ L.d("Logout complete.") }) { L.e(it) }
    }

    private fun enableWebViewDebugging() {
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    fun unmarshalTheme(themeId: Int): Theme {
        var result = Theme.ofMarshallingId(themeId)
        if (result == null) {
            L.d("Theme id=$themeId is invalid, using fallback.")
            result = Theme.fallback
        }
        return result
    }

    private fun initTabs() {
        if (Prefs.hasTabs) {
            tabList.addAll(Prefs.tabs)
        }
        if (tabList.isEmpty()) {
            tabList.add(Tab())
        }
    }

    companion object {
        lateinit var instance: WikipediaApp
            private set
    }
}
