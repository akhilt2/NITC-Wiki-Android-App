package org.akhil.nitcwiki.views

import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

/**
 * Triggers events when one or more EditTexts are empty or not
 */
class NonEmptyValidator(private val actionButton: Button, private vararg val textInputs: TextInputLayout) {

    private var lastIsValidValue = false

    init {
        textInputs.forEach {
            it.editText!!.doAfterTextChanged { revalidate() }
        }
        revalidate(true)
    }

    private fun revalidate(force: Boolean = false) {
        val isValid = textInputs.all { it.editText!!.text.isNotEmpty() }
        if (isValid != lastIsValidValue || force) {
            lastIsValidValue = isValid
            actionButton.isEnabled = lastIsValidValue
            actionButton.alpha = if (lastIsValidValue) 1.0f else 0.5f
        }
    }
}
