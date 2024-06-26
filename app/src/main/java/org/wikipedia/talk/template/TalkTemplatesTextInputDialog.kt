package org.akhil.nitcwiki.talk.template

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.DialogTalkTemplatesTextInputBinding

class TalkTemplatesTextInputDialog(
    activity: Activity,
    positiveButtonText: Int = R.string.text_input_dialog_ok_button_text,
    negativeButtonText: Int = R.string.text_input_dialog_cancel_button_text,
    isExisting: Boolean = false
) : MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme_Input) {
    interface Callback {
        fun onSuccess(subjectText: String)
        fun onCancel()
        fun onTextChanged(text: String, dialog: TalkTemplatesTextInputDialog)
        fun onDismiss()
        fun getSubjectText(): String
    }

    private var binding = DialogTalkTemplatesTextInputBinding.inflate(LayoutInflater.from(context))
    private var dialog: AlertDialog? = null
    var callback: Callback? = null
    val isSaveAsNewChecked get() = binding.dialogSaveAsNewRadio.isChecked
    val isSaveExistingChecked get() = binding.dialogSaveExistingRadio.isChecked

    init {
        setView(binding.root)

        setPositiveButton(positiveButtonText) { _, _ ->
            callback?.onSuccess(binding.subjectInput.text.toString())
        }
        setNegativeButton(negativeButtonText) { _, _ ->
            callback?.onCancel()
        }
        setOnDismissListener {
            callback?.onDismiss()
        }
        binding.subjectInput.doOnTextChanged { text, _, _, _ ->
            callback?.onTextChanged(text.toString(), this)
        }
        binding.dialogSaveExistingRadio.isVisible = isExisting
        binding.dialogSaveExistingRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                callback?.onTextChanged(binding.subjectInput.text.toString(), this)
            }
        }
    }

    override fun create(): AlertDialog {
        dialog = super.create()
        dialog?.setOnShowListener {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            binding.dialogSaveAsNewRadio.isChecked = true
        }
        binding.subjectInput.setText(callback?.getSubjectText())
        return dialog!!
    }

    fun setError(text: CharSequence?) {
        binding.subjectInputContainer.error = text
    }

    fun setPositiveButtonEnabled(enabled: Boolean) {
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = enabled
    }

    fun getView(): ViewGroup {
        return binding.root
    }
}
