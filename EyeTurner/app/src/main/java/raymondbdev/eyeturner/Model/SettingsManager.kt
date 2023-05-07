package raymondbdev.eyeturner.Model

import raymondbdev.eyeturner.Model.enums.VibrateSetting

class SettingsManager {
    private var vibrateSetting = VibrateSetting.LIGHT
    private val availableSizes = arrayOf(14, 18, 22)
    private val availableStringSizes = arrayOf(2000, 1600, 1200)

    private var sizeIndex = 0

    var maxStringSize: Int = 1
        get() = availableStringSizes[sizeIndex]

    var fontSize: Int = 1
        get() = availableSizes[sizeIndex]

    companion object {
        // Key: font size, Value: max string length
        private val sizesMap = hashMapOf(14 to 2000, 18 to 1600, 20 to 1200, 24 to 800, 30 to 600)
        fun getMaxStringSizeByFontSize(fontSize: Int): Int? {
            return sizesMap[fontSize]
        }
    }

    fun getColourMode(): String? {

        return if (vibrateSetting == VibrateSetting.LIGHT) {
            "Light"
        } else if (vibrateSetting == VibrateSetting.OFF) {
            "Off"
        } else {
            null
        }

    }



    fun switchColourMode() {
        if (vibrateSetting == VibrateSetting.OFF) {
            vibrateSetting = VibrateSetting.LIGHT
        } else {
            vibrateSetting = VibrateSetting.OFF
        }
    }

    fun getPreviousFontSize(): Int {
        if (sizeIndex == 0) {
            return availableSizes[0]
        }

        sizeIndex--

        return fontSize
    }

    fun getNextFontSize(): Int {
        if (sizeIndex == availableSizes.size - 1) {
            return availableSizes[availableSizes.size - 1]
        }

        sizeIndex++

        return fontSize
    }

    fun switchVibrateSetting() {

    }
}