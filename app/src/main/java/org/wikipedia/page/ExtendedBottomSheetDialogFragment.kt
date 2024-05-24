package org.akhil.nitcwiki.page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.analytics.BreadcrumbsContextHelper
import org.akhil.nitcwiki.analytics.eventplatform.BreadCrumbLogEvent
import org.akhil.nitcwiki.util.DeviceUtil
import org.akhil.nitcwiki.util.ResourceUtil

open class ExtendedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BreadCrumbLogEvent.logScreenShown(requireContext(), this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ExtendedBottomSheetDialog(requireContext(), theme)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            DeviceUtil.setNavigationBarColor(it, ResourceUtil.getThemedColor(requireContext(), R.attr.paper_color))
        }
    }

    class ExtendedBottomSheetDialog(context: Context, @StyleRes theme: Int) :
        BottomSheetDialog(context, theme) {

        override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
            window?.let {
                BreadcrumbsContextHelper.dispatchTouchEvent(it, ev)
            }
            return super.dispatchTouchEvent(ev)
        }
    }
}
