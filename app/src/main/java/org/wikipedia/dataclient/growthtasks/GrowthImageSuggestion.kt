package org.akhil.nitcwiki.dataclient.growthtasks

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class GrowthImageSuggestion(
    val titleNamespace: Int = 0,
    val titleText: String = "",
    val datasetId: String = "",
    val images: List<ImageItem> = emptyList()
) {
    @Serializable
    class ImageItem(
        val image: String = "",
        val displayFilename: String = "",
        val source: String = "",
        val projects: List<String> = emptyList(),
        val metadata: ImageMetadata? = null
    )

    @Serializable
    class ImageMetadata(
        val descriptionUrl: String = "",
        val thumbUrl: String = "",
        val fullUrl: String = "",
        val originalWidth: Int = 0,
        val originalHeight: Int = 0,
        val mediaType: String = "",
        val description: String = "",
        val author: String = "",
        val license: String = "",
        val date: String = "",
        val caption: String = "",
        val categories: List<String> = emptyList(),
        val reason: String = "",
        val contentLanguageName: String = ""
    )

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    class AddImageFeedbackBody constructor(
        val token: String = "",
        val editRevId: Long = 0,
        val filename: String = "",
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) val accepted: Boolean = false,
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) val reasons: List<String> = emptyList(),
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) val caption: String? = null,
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) val sectionTitle: String? = null,
        @EncodeDefault(EncodeDefault.Mode.ALWAYS) val sectionNumber: Int? = null
    )
}
