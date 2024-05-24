package org.akhil.nitcwiki.mlkit

class MlKitLanguageDetector {
    interface Callback {
        fun onLanguageDetectionSuccess(languageCodes: List<String>)
    }

    var callback: Callback? = null

    fun detectLanguageFromText(text: String) {
        // stub
    }
}
