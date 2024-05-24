package org.akhil.nitcwiki.theme

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.databinding.FragmentThemeFittingRoomBinding
import org.akhil.nitcwiki.events.ChangeTextSizeEvent
import org.akhil.nitcwiki.events.WebViewInvalidateEvent
import org.akhil.nitcwiki.settings.Prefs

class ThemeFittingRoomFragment : Fragment() {
    private var _binding: FragmentThemeFittingRoomBinding? = null
    private val binding get() = _binding!!
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentThemeFittingRoomBinding.inflate(inflater, container, false)
        disposables.add(WikipediaApp.instance.bus.subscribe(EventBusConsumer()))
        binding.themeTestImage.loadImage(R.drawable.w_nav_mark)
        updateTextSize()
        updateFontFamily()
        return binding.root
    }

    override fun onDestroyView() {
        disposables.clear()
        _binding = null
        super.onDestroyView()
    }

    private fun updateTextSize() {
        val titleMultiplier = 1.6f
        val fontSize = WikipediaApp.instance.getFontSize(requireActivity().window)
        binding.themeTestText.textSize = fontSize
        binding.themeTestTitle.textSize = fontSize * titleMultiplier
    }

    private fun updateFontFamily() {
        val currentTypeface = if (Prefs.fontFamily == resources.getString(R.string.font_family_sans_serif)) Typeface.SANS_SERIF else Typeface.SERIF
        binding.themeTestTitle.typeface = currentTypeface
        binding.themeTestText.typeface = currentTypeface
    }

    private inner class EventBusConsumer : Consumer<Any> {
        override fun accept(event: Any) {
            if (event is ChangeTextSizeEvent) {
                updateTextSize()
                binding.themeTestText.post { WikipediaApp.instance.bus.post(WebViewInvalidateEvent()) }
            }
        }
    }

    companion object {
        fun newInstance(): ThemeFittingRoomFragment {
            return ThemeFittingRoomFragment()
        }
    }
}
