package raymondbdev.eyeturner.Model

import raymondbdev.eyeturner.Model.Enums.ColourMode

class SettingsManager {
    private var colourMode = ColourMode.LIGHT_MODE
    private val availableSizes = arrayOf(14, 18, 20, 24, 30)
    private val availableStringSizes = arrayOf(2000, 1600, 1200, 800, 600)
    private var sizeIndex = 0

    var maxStringSize: Int = 2500
        get() = availableStringSizes[sizeIndex]

    var fontSize: Int = 14
        get() = availableSizes[sizeIndex]

    fun getColourMode(): String? {

        return if (colourMode == ColourMode.LIGHT_MODE) {
            "Light"
        } else if (colourMode == ColourMode.DARK_MODE) {
            "Dark"
        } else {
            null
        }

    }

    fun switchColourMode() {
        if (colourMode == ColourMode.DARK_MODE) {
            colourMode = ColourMode.LIGHT_MODE
        } else {
            colourMode = ColourMode.DARK_MODE
        }
    }

    fun getPreviousFontSize(): Int {

        if (sizeIndex == 0) {
            return availableSizes[0]
        }

        sizeIndex--
        fontSize = availableSizes[sizeIndex]
        maxStringSize = availableStringSizes[sizeIndex]

        return fontSize
    }

    fun getNextFontSize(): Int {

        if (sizeIndex == availableSizes.size - 1) {
            return availableSizes[availableSizes.size - 1]
        }

        sizeIndex++
        fontSize = availableSizes[sizeIndex]
        maxStringSize = availableStringSizes[sizeIndex]

        return fontSize
    }
}