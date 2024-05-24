package org.akhil.nitcwiki.feed.topread

import android.app.ActivityOptions
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.Constants.InvokeSource
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.FragmentMostReadBinding
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.view.ListCardItemView
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.PageActivity
import org.akhil.nitcwiki.readinglist.ReadingListBehaviorsUtil
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.L10nUtil
import org.akhil.nitcwiki.util.TabUtil
import org.akhil.nitcwiki.views.DefaultRecyclerAdapter
import org.akhil.nitcwiki.views.DefaultViewHolder
import org.akhil.nitcwiki.views.DrawableItemDecoration

class TopReadFragment : Fragment() {

    private var _binding: FragmentMostReadBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopReadViewModel by viewModels { TopReadViewModel.Factory(requireActivity().intent.extras!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMostReadBinding.inflate(inflater, container, false)

        val card = viewModel.card

        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar?.title = getString(R.string.top_read_activity_title, card.subtitle())

        L10nUtil.setConditionalLayoutDirection(binding.root, card.wikiSite().languageCode)

        binding.mostReadRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.mostReadRecyclerView.addItemDecoration(DrawableItemDecoration(requireContext(), R.attr.list_divider))
        binding.mostReadRecyclerView.isNestedScrollingEnabled = false
        binding.mostReadRecyclerView.adapter = RecyclerAdapter(card.items(), Callback())

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private val appCompatActivity get() = requireActivity() as AppCompatActivity

    private class RecyclerAdapter constructor(items: List<TopReadItemCard>, private val callback: Callback) :
        DefaultRecyclerAdapter<TopReadItemCard, ListCardItemView>(items) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder<ListCardItemView> {
            return DefaultViewHolder(ListCardItemView(parent.context))
        }

        override fun onBindViewHolder(holder: DefaultViewHolder<ListCardItemView>, position: Int) {
            val card = item(position)
            holder.view.setCard(card).setHistoryEntry(HistoryEntry(card.pageTitle,
                HistoryEntry.SOURCE_FEED_MOST_READ_ACTIVITY)).setCallback(callback)
        }
    }

    private inner class Callback : ListCardItemView.Callback {
        override fun onSelectPage(card: Card, entry: HistoryEntry, openInNewBackgroundTab: Boolean) {
            if (openInNewBackgroundTab) {
                TabUtil.openInNewBackgroundTab(entry)
                FeedbackUtil.showMessage(requireActivity(), R.string.article_opened_in_background_tab)
            } else {
                startActivity(PageActivity.newIntentForNewTab(requireContext(), entry, entry.title))
            }
        }

        override fun onSelectPage(card: Card, entry: HistoryEntry, sharedElements: Array<Pair<View, String>>) {
            val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), *sharedElements)
            val intent = PageActivity.newIntentForNewTab(requireContext(), entry, entry.title)
            if (sharedElements.isNotEmpty()) {
                intent.putExtra(Constants.INTENT_EXTRA_HAS_TRANSITION_ANIM, true)
            }
            startActivity(intent, if (DimenUtil.isLandscape(requireContext()) || sharedElements.isEmpty()) null else options.toBundle())
        }

        override fun onAddPageToList(entry: HistoryEntry, addToDefault: Boolean) {
            ReadingListBehaviorsUtil.addToDefaultList(requireActivity(), entry.title, addToDefault, InvokeSource.MOST_READ_ACTIVITY)
        }

        override fun onMovePageToList(sourceReadingListId: Long, entry: HistoryEntry) {
            ReadingListBehaviorsUtil.moveToList(requireActivity(), sourceReadingListId, entry.title, InvokeSource.MOST_READ_ACTIVITY)
        }
    }

    companion object {
        fun newInstance(card: TopReadListCard): TopReadFragment {
            return TopReadFragment().apply {
                arguments = bundleOf(TopReadArticlesActivity.TOP_READ_CARD to card)
            }
        }
    }
}
