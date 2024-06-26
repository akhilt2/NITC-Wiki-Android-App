package org.akhil.nitcwiki.usercontrib

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.analytics.eventplatform.PatrollerExperienceEvent
import org.akhil.nitcwiki.databinding.DialogUserInformationBinding
import org.akhil.nitcwiki.suggestededits.SuggestedEditsRecentEditsActivity
import org.akhil.nitcwiki.suggestededits.SuggestionsActivity
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.Resource
import org.akhil.nitcwiki.util.StringUtil
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class UserInformationDialog : DialogFragment() {

    private val viewModel: UserInformationDialogViewModel by viewModels { UserInformationDialogViewModel.Factory(requireArguments()) }

    private var _binding: DialogUserInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogUserInformationBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.patroller_tasks_edits_list_user_information_dialog_title)
            .setView(binding.root)
            .setPositiveButton(R.string.patroller_tasks_edits_list_user_information_dialog_close) { _, _ ->
                dismiss()
            }.run {
                setupDialog()
                this
            }.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDialog() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect {
                    when (it) {
                        is Resource.Loading -> onLoading()
                        is Resource.Success -> onSuccess(it.data.first, it.data.second)
                        is Resource.Error -> onError(it.throwable)
                    }
                }
            }
        }
        binding.dialogErrorView.backClickListener = View.OnClickListener {
            dismiss()
        }
        binding.dialogErrorView.retryClickListener = View.OnClickListener {
            dismiss()
        }
    }

    private fun onLoading() {
        binding.userInformationContainer.isVisible = false
        binding.dialogProgressBar.isVisible = true
        binding.dialogErrorView.isVisible = false
    }

    private fun onSuccess(editCount: String, registrationDate: Date) {
        sendPatrollerExperienceEvent()
        binding.userInformationContainer.isVisible = true
        binding.dialogProgressBar.isVisible = false
        binding.dialogErrorView.isVisible = false
        val localDate = LocalDateTime.ofInstant(registrationDate.toInstant(), ZoneId.systemDefault()).toLocalDate()
        val dateStr = DateUtil.getShortDateString(localDate)
        binding.userTenure.text = StringUtil.fromHtml(getString(R.string.patroller_tasks_edits_list_user_information_dialog_joined_date_text, dateStr))
        binding.editCount.text = StringUtil.fromHtml(getString(R.string.patroller_tasks_edits_list_user_information_dialog_edit_count_text, editCount))
    }

    private fun sendPatrollerExperienceEvent() {
        if (requireActivity() is SuggestionsActivity ||
            requireActivity() is SuggestedEditsRecentEditsActivity) {
            PatrollerExperienceEvent.logAction("user_info_impression",
                if (requireActivity() is SuggestedEditsRecentEditsActivity) "pt_recent_changes" else "pt_edit")
        }
    }

    private fun onError(t: Throwable) {
        binding.userInformationContainer.isVisible = false
        binding.dialogProgressBar.isVisible = false
        binding.dialogErrorView.setError(t)
        binding.dialogErrorView.isVisible = true
    }

    companion object {

        const val USERNAME_ARG = "username"

        fun newInstance(username: String): UserInformationDialog {
            val dialog = UserInformationDialog()
            dialog.arguments = bundleOf(USERNAME_ARG to username)
            return dialog
        }
    }
}
