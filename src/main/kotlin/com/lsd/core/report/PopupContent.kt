package com.lsd.core.report

import com.lsd.core.IdGenerator

object PopupContent {

    private val idGenerator = IdGenerator()

    /**
     * Convenience method to create the html for a hyperlink that is linked to a popup containing the provided content.
     * 
     * @param id The id is random by default and usually won't need to be overridden unless a specific id is required for some reason.
     * @param popupTitle The popupTitle is displayed as the title on the popup box containing the provided content. It's optional.
     * @param hyperlinkText The text that gets displayed on the hyperlink.
     * @param popupContent The content that will be hidden until a user clicks on the hyperlink.
     */
    @JvmStatic
    fun popupHyperlink(
        id: String = idGenerator.next(),
        popupTitle: String = "",
        hyperlinkText: String,
        popupContent: String
    ): String {
        return """
            <a href="#$id">$hyperlinkText</a>
            <div id="$id" class="overlay" onclick="location.href='#!';">
                <div class="popup" onclick="event.stopPropagation();">
                    <h2>$popupTitle</h2>
                    <a class="close" href="#!">&times;</a>
                    <div class="content">$popupContent</div>
                </div>
            </div>
        """.trimMargin()
    }

}