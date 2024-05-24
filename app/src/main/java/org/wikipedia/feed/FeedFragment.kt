package org.akhil.nitcwiki.feed

import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.akhil.nitcwiki.BackPressedHandler
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.activity.FragmentUtil.getCallback
import org.akhil.nitcwiki.databinding.FragmentFeedBinding
import org.akhil.nitcwiki.feed.FeedCoordinatorBase.FeedUpdateListener
import org.akhil.nitcwiki.feed.configure.ConfigureActivity
import org.akhil.nitcwiki.feed.configure.ConfigureItemLanguageDialogView
import org.akhil.nitcwiki.feed.configure.LanguageItemAdapter
import org.akhil.nitcwiki.feed.image.FeaturedImage
import org.akhil.nitcwiki.feed.image.FeaturedImageCard
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.WikiSiteCard
import org.akhil.nitcwiki.feed.news.NewsCard
import org.akhil.nitcwiki.feed.news.NewsItemView
import org.akhil.nitcwiki.feed.random.RandomCardView
import org.akhil.nitcwiki.feed.topread.TopReadArticlesActivity
import org.akhil.nitcwiki.feed.topread.TopReadListCard
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.language.AppLanguageLookUpTable
import org.akhil.nitcwiki.random.RandomActivity
import org.akhil.nitcwiki.readinglist.sync.ReadingListSyncAdapter
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.settings.SettingsActivity
import org.akhil.nitcwiki.settings.languages.WikipediaLanguagesActivity
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.util.UriUtil

class FeedFragment : Fragment(), BackPressedHandler {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var feedAdapter: FeedAdapter<View>
    private val feedCallback = FeedCallback()
    private val feedScrollListener = FeedScrollListener()
    private val callback get() = getCallback(this, Callback::class.java)
    private var app: WikipediaApp = WikipediaApp.instance
    private var coordinator: FeedCoordinator = FeedCoordinator(app)
    private var shouldElevateToolbar = false

    interface Callback {
        fun onFeedSearchRequested(view: View)
        fun onFeedVoiceSearchRequested()
        fun onFeedSelectPage(entry: HistoryEntry, openInNewBackgroundTab: Boolean)
        fun onFeedSelectPageWithAnimation(entry: HistoryEntry, sharedElements: Array<Pair<View, String>>)
        fun onFeedAddPageToList(entry: HistoryEntry, addToDefault: Boolean)
        fun onFeedMovePageToList(sourceReadingListId: Long, entry: HistoryEntry)
        fun onFeedNewsItemSelected(card: NewsCard, view: NewsItemView)
        fun onFeedSeCardFooterClicked()
        fun onFeedShareImage(card: FeaturedImageCard)
        fun onFeedDownloadImage(image: FeaturedImage)
        fun onFeaturedImageSelected(card: FeaturedImageCard)
        fun onLoginRequested()
        fun updateToolbarElevation(elevate: Boolean)
    }

    private val requestFeedConfigurationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == SettingsActivity.ACTIVITY_RESULT_FEED_CONFIGURATION_CHANGED) {
            coordinator.updateHiddenCards()
            refresh()
        }
    }

    private val requestLanguageChangeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == SettingsActivity.ACTIVITY_RESULT_LANGUAGE_CHANGED) {
            refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coordinator.more(app.wikiSite)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        feedAdapter = FeedAdapter(coordinator, feedCallback)
        binding.feedView.adapter = feedAdapter
        binding.feedView.addOnScrollListener(feedScrollListener)
        binding.swipeRefreshLayout.setColorSchemeResources(ResourceUtil.getThemedAttributeId(requireContext(), R.attr.progressive_color))
        binding.swipeRefreshLayout.setOnRefreshListener { refresh() }
        binding.customizeButton.setOnClickListener { showConfigureActivity(-1) }
        coordinator.setFeedUpdateListener(object : FeedUpdateListener {
            override fun insert(card: Card, pos: Int) {
                if (isAdded) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    feedAdapter.notifyItemInserted(pos)
                }
            }

            override fun remove(card: Card, pos: Int) {
                if (isAdded) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    feedAdapter.notifyItemRemoved(pos)
                }
            }

            override fun finished(shouldUpdatePreviousCard: Boolean) {
                if (!isAdded) {
                    return
                }
                if (feedAdapter.itemCount < 2) {
                    binding.emptyContainer.visibility = View.VISIBLE
                } else {
                    binding.emptyContainer.visibility = View.GONE
                    if (shouldUpdatePreviousCard) {
                        feedAdapter.notifyItemChanged(feedAdapter.itemCount - 1)
                    }
                }
            }
        })
        callback?.updateToolbarElevation(shouldElevateToolbar())
        ReadingListSyncAdapter.manualSync()
        Prefs.incrementExploreFeedVisitCount()
        return binding.root
    }

    private fun showRemoveChineseVariantPrompt() {
        if (app.languageState.appLanguageCodes.contains(AppLanguageLookUpTable.TRADITIONAL_CHINESE_LANGUAGE_CODE) &&
            app.languageState.appLanguageCodes.contains(AppLanguageLookUpTable.SIMPLIFIED_CHINESE_LANGUAGE_CODE) &&
            Prefs.shouldShowRemoveChineseVariantPrompt) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.dialog_of_remove_chinese_variants_from_app_lang_title)
                .setMessage(R.string.dialog_of_remove_chinese_variants_from_app_lang_text)
                .setPositiveButton(R.string.dialog_of_remove_chinese_variants_from_app_lang_edit) { _, _ -> showLanguagesActivity(InvokeSource.LANG_VARIANT_DIALOG) }
                .setNegativeButton(R.string.dialog_of_remove_chinese_variants_from_app_lang_no, null)
                .show()
        }
        Prefs.shouldShowRemoveChineseVariantPrompt = false
    }

    override fun onResume() {
        super.onResume()
        showRemoveChineseVariantPrompt()

        // Explicitly invalidate the feed adapter, since it occasionally crashes the StaggeredGridLayout
        // on certain devices. (TODO: investigate further)
        feedAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        coordinator.setFeedUpdateListener(null)
        binding.swipeRefreshLayout.setOnRefreshListener(null)
        binding.feedView.removeOnScrollListener(feedScrollListener)
        binding.feedView.adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        coordinator.reset()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    fun shouldElevateToolbar(): Boolean {
        return shouldElevateToolbar
    }

    fun scrollToTop() {
        binding.feedView.smoothScrollToPosition(0)
    }

    fun onGoOffline() {
        feedAdapter.notifyDataSetChanged()
        coordinator.requestOfflineCard()
    }

    fun onGoOnline() {
        feedAdapter.notifyDataSetChanged()
        coordinator.removeOfflineCard()
        coordinator.incrementAge()
        coordinator.more(app.wikiSite)
    }

    fun refresh() {
        binding.emptyContainer.visibility = View.GONE
        coordinator.reset()
        feedAdapter.notifyDataSetChanged()
        coordinator.more(app.wikiSite)
    }

    fun updateHiddenCards() {
        coordinator.updateHiddenCards()
    }

    private inner class FeedCallback : FeedAdapter.Callback {
        override fun onRequestMore() {
            binding.feedView.post {
                if (isAdded) {
                    coordinator.incrementAge()
                    coordinator.more(app.wikiSite)
                }
            }
        }

        override fun onRetryFromOffline() {
            refresh()
        }

        override fun onError(t: Throwable) {
            FeedbackUtil.showError(requireActivity(), t)
        }

        override fun onSelectPage(card: Card, entry: HistoryEntry, openInNewBackgroundTab: Boolean) {
            callback?.onFeedSelectPage(entry, openInNewBackgroundTab)
        }

        override fun onSelectPage(card: Card, entry: HistoryEntry, sharedElements: Array<Pair<View, String>>) {
            callback?.onFeedSelectPageWithAnimation(entry, sharedElements)
        }

        override fun onAddPageToList(entry: HistoryEntry, addToDefault: Boolean) {
            callback?.onFeedAddPageToList(entry, addToDefault)
        }

        override fun onMovePageToList(sourceReadingListId: Long, entry: HistoryEntry) {
            callback?.onFeedMovePageToList(sourceReadingListId, entry)
        }

        override fun onSearchRequested(view: View) {
            callback?.onFeedSearchRequested(view)
        }

        override fun onVoiceSearchRequested() {
            callback?.onFeedVoiceSearchRequested()
        }

        override fun onRequestDismissCard(card: Card): Boolean {
            val position = coordinator.dismissCard(card)
            if (position < 0) {
                return false
            }
            showDismissCardUndoSnackbar(card, position)
            return true
        }

        override fun onRequestEditCardLanguages(card: Card) {
            showCardLangSelectDialog(card)
        }

        override fun onRequestCustomize(card: Card) {
            showConfigureActivity(card.type().code())
        }

        override fun onNewsItemSelected(card: NewsCard, view: NewsItemView) {
            callback?.onFeedNewsItemSelected(card, view)
        }

        override fun onShareImage(card: FeaturedImageCard) {
            callback?.onFeedShareImage(card)
        }

        override fun onDownloadImage(image: FeaturedImage) {
            callback?.onFeedDownloadImage(image)
        }

        override fun onFeaturedImageSelected(card: FeaturedImageCard) {
            callback?.onFeaturedImageSelected(card)
        }

        override fun onAnnouncementPositiveAction(card: Card, uri: Uri) {
            when {
                uri.toString() == UriUtil.LOCAL_URL_LOGIN -> callback?.onLoginRequested()
                uri.toString() == UriUtil.LOCAL_URL_SETTINGS -> requestLanguageChangeLauncher.launch(SettingsActivity.newIntent(requireContext()))
                uri.toString() == UriUtil.LOCAL_URL_CUSTOMIZE_FEED -> {
                    showConfigureActivity(card.type().code())
                    onRequestDismissCard(card)
                }
                uri.toString() == UriUtil.LOCAL_URL_LANGUAGES -> showLanguagesActivity(InvokeSource.ANNOUNCEMENT)
                else -> UriUtil.handleExternalLink(requireContext(), uri)
            }
        }

        override fun onAnnouncementNegativeAction(card: Card) {
            onRequestDismissCard(card)
        }

        override fun onRandomClick(view: RandomCardView) {
            view.card?.let {
                startActivity(RandomActivity.newIntent(requireActivity(), it.wikiSite(), InvokeSource.FEED))
            }
        }

        override fun onFooterClick(card: Card) {
            if (card is TopReadListCard) {
                startActivity(TopReadArticlesActivity.newIntent(requireContext(), card))
            }
        }

        override fun onSeCardFooterClicked() {
            callback?.onFeedSeCardFooterClicked()
        }
    }

    private inner class FeedScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val shouldElevate = binding.feedView.firstVisibleItemPosition != 0
            if (shouldElevate != shouldElevateToolbar) {
                shouldElevateToolbar = shouldElevate
                requireActivity().invalidateOptionsMenu()
                callback?.updateToolbarElevation(shouldElevateToolbar())
            }
        }
    }

    private fun showDismissCardUndoSnackbar(card: Card, position: Int) {
        val snackbar = FeedbackUtil.makeSnackbar(requireActivity(), getString(R.string.menu_feed_card_dismissed))
        snackbar.setAction(R.string.reading_list_item_delete_undo) { coordinator.undoDismissCard(card, position) }
        snackbar.show()
    }

    private fun showCardLangSelectDialog(card: Card) {
        val contentType = card.type().contentType()
        if (contentType != null && contentType.isPerLanguage) {
            val adapter = LanguageItemAdapter(requireContext(), contentType)
            val view = ConfigureItemLanguageDialogView(requireContext())
            val tempDisabledList = ArrayList(contentType.langCodesDisabled)
            view.setContentType(adapter.langList, tempDisabledList)
            MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(contentType.titleId)
                .setPositiveButton(R.string.feed_lang_selection_dialog_ok_button_text) { _, _ ->
                    contentType.langCodesDisabled.clear()
                    contentType.langCodesDisabled.addAll(tempDisabledList)
                    refresh()
                }
                .setNegativeButton(R.string.feed_lang_selection_dialog_cancel_button_text, null)
                .show()
        }
    }

    private fun showConfigureActivity(invokeSource: Int) {
        requestFeedConfigurationLauncher.launch(ConfigureActivity.newIntent(requireActivity(), invokeSource))
    }

    private fun showLanguagesActivity(invokeSource: InvokeSource) {
        requestLanguageChangeLauncher.launch(WikipediaLanguagesActivity.newIntent(requireActivity(), invokeSource))
    }

    private fun getCardLanguageCode(card: Card?): String? {
        return if (card is WikiSiteCard) card.wikiSite().languageCode else null
    }

    companion object {
        fun newInstance(): FeedFragment {
            return FeedFragment().apply {
                retainInstance = true
            }
        }
    }
}
