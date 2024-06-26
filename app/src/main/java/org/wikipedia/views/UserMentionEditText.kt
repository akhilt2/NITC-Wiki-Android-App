package org.akhil.nitcwiki.views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.text.buildSpannedString
import androidx.core.widget.doOnTextChanged
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.edit.SyntaxHighlightableEditText
import org.akhil.nitcwiki.staticdata.UserAliasData
import org.akhil.nitcwiki.util.log.L

class UserMentionEditText : SyntaxHighlightableEditText {
    interface Listener {
        fun onStartUserNameEntry()
        fun onCancelUserNameEntry()
        fun onUserNameChanged(userName: String)
    }

    var listener: Listener? = null

    private val textWatcher: TextWatcher?
    private var userNameStartPos = -1
    private var userNameEndPos = -1
    private val isEnteringUserName get() = userNameStartPos >= 0
    private var spacesPressedCount = 0
    private var isUserNameCommitting = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {

        // This is a state machine that behaves roughly as follows:
        // * When the "@" symbol is typed, it enters a state of inputting a username.
        // * During the username input state, every subsequent keypress will send an event to the
        //   listener, to give it a chance to show a dropdown selection for searching usernames.
        // * If the user selects a final username from the dropdown selection, the input state is
        //   finished, and the username is turned into a special Span that contains the username at
        //   that position.
        // * If a space " " character is pressed, without having selected a username from the
        //   dropdown list, the input state is cancelled, and no special Span is added.
        // * For all other keypresses, while not in the username input state, it behaves as usual.
        textWatcher = doOnTextChanged { text, start, before, count ->
            if (text == null) {
                return@doOnTextChanged
            }
            if (count == 1 && start < text.length && text[start] == '@' &&
                    (start == 0 || (start > 0 && (text[start - 1] == ' ' || text[start - 1] == '\r' || text[start - 1] == '\n'))) &&
                    !isEnteringUserName) {
                userNameStartPos = start
                userNameEndPos = userNameStartPos
                onStartUserNameEntry()
            }

            if (isEnteringUserName) {
                if (count - before == 1 && start + count - 1 < text.length && start + count - 1 >= 0) {
                    val enterPressed = text[start + count - 1] == '\r' || text[start + count - 1] == '\n'
                    val spacePressed = text[start + count - 1] == ' '

                    if (enterPressed) {
                        onCancelUserNameEntry()
                    } else {
                        if (spacePressed) {
                            spacesPressedCount++
                        }

                        if (spacePressed && spacesPressedCount > 1) {
                            onCancelUserNameEntry()
                        } else {
                            userNameEndPos += (count - before)
                        }
                        if (userNameEndPos <= userNameStartPos) {
                            onCancelUserNameEntry()
                        }
                    }
                }
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (textWatcher == null || isUserNameCommitting) {
            return
        }
        if (isEnteringUserName) {
            if ((selStart < userNameStartPos || selEnd > userNameEndPos) ||
                    (userNameEndPos > text.length)) {
                onCancelUserNameEntry()
                return
            }
            onUserNameChanged(text.substring(userNameStartPos, userNameEndPos))
        }
    }

    private fun onStartUserNameEntry() {
        listener?.onStartUserNameEntry()
        spacesPressedCount = 0
    }

    private fun onCancelUserNameEntry() {
        userNameStartPos = -1
        userNameEndPos = -1
        spacesPressedCount = 0
        listener?.onCancelUserNameEntry()
    }

    private fun onUserNameChanged(userName: String) {
        L.d("Entering username: $userName")
        listener?.onUserNameChanged(userName)
    }

    fun prepopulateUserName(userName: String, wikiSite: WikiSite) {
        isUserNameCommitting = true
        val spannedString = buildSpannedString {
            append(composeUserNameLink(userName, wikiSite))
            append(" ")
        }
        setText(spannedString)
        isUserNameCommitting = false
        setSelection(spannedString.length)
    }

    fun onCommitUserName(userName: String, wikiSite: WikiSite) {
        try {
            isUserNameCommitting = true
            if (userNameStartPos < 0 || userNameEndPos <= userNameStartPos) {
                onCancelUserNameEntry()
                return
            }

            val initialString = text.subSequence(0, userNameStartPos)
            val userNameString = composeUserNameLink(userName, wikiSite)
            val spannedString = buildSpannedString {
                append(initialString)
                append(userNameString)
                if (userNameEndPos < text.length) {
                    append(text.subSequence(userNameEndPos, text.length - 1))
                }
            }

            setText(spannedString)
            setSelection((initialString.toString() + userNameString).length)
            onCancelUserNameEntry()
        } finally {
            isUserNameCommitting = false
        }
    }

    private fun composeUserNameLink(userName: String, wikiSite: WikiSite): String {
        return "@[[" + UserAliasData.valueFor(wikiSite.languageCode) + ":" + userName + "|" + userName + "]]"
    }
}
