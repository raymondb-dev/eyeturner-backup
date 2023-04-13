package raymondbdev.eyeturner.Model

import raymondbdev.eyeturner.Model.Enums.ColourMode

class SettingsManager {
    private var colourMode = ColourMode.LIGHT_MODE
    private val availableSizes = arrayOf(8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24)
    private var sizeIndex = 4

    // TODO: convert font size to relative

    val fontSize: Int
        get() = availableSizes[sizeIndex]

    fun getColourMode(): String? {
        if (colourMode == ColourMode.LIGHT_MODE) {
            return "Light"
        } else if (colourMode == ColourMode.DARK_MODE) {
            return "Dark"
        }
        return null
    }

    fun switchColourMode() {
        colourMode = if (colourMode == ColourMode.DARK_MODE) {
            ColourMode.LIGHT_MODE
        } else {
            ColourMode.DARK_MODE
        }
    }

    val nextFontSize: Int
        get() {
            if (sizeIndex == availableSizes.size - 1) {
                return availableSizes[availableSizes.size - 1]
            }
            sizeIndex++
            return availableSizes[sizeIndex]
        }
    val previousFontSize: Int
        get() {
            if (sizeIndex == 0) {
                return availableSizes[0]
            }
            sizeIndex--
            return availableSizes[sizeIndex]
        }
}