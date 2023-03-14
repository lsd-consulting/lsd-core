package com.lsd.core.report.model

/**
 * This type of event holds extra data in addition to what is displayed on the diagram and therefore
 * we can use popups etc. to display this data.
 */
data class DataHolder(val id: String, val abbreviatedLabel: String, val data: Any?) 