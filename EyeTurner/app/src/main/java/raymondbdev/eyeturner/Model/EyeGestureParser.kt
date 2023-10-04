package raymondbdev.eyeturner.Model

import android.util.Log
import camp.visual.gazetracker.filter.OneEuroFilterManager
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.enums.EyeGesture


/**
 * Contains functions for parsing Eye Gestures.
 */
class EyeGestureParser {
    // Gesture boundaries for the phone (tested on Google Pixel 6 Pro)
    var LEFT_BOUNDARY = -1100
    var RIGHT_BOUNDARY = 700
    var TOP_BOUNDARY = -700
    var BOTTOM_BOUNDARY = 2400

    private var lastLocationX = 0.0
    private var lastLocationY = 0.0

    private val oneEuroFilterManager = OneEuroFilterManager(2)

    fun calculateEyeGesture(gazeInfo: GazeInfo): EyeGesture {
        var xPos = gazeInfo.x
        var yPos = gazeInfo.y
        var coordinatesStr = String.format("x - $xPos y - $yPos")

        if (oneEuroFilterManager.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
            val filteredValues = oneEuroFilterManager.filteredValues
            val filteredX = filteredValues[0]
            val filteredY = filteredValues[1]

            xPos = filteredX
            yPos = filteredY

            // coordinatesStr = String.format("x - $xPos vs. filteredX - $filteredX : y - $yPos vs. filteredY - $filteredY")
        }

        // accepts eye Gestures if movement from the boundary is detected.
        if (xPos > LEFT_BOUNDARY && lastLocationX < LEFT_BOUNDARY) {
            Log.i("Eye Gesture Performed", "LOOK_LEFT: $coordinatesStr")
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()

            return EyeGesture.LOOK_LEFT

        } else if (xPos < RIGHT_BOUNDARY && lastLocationX > RIGHT_BOUNDARY) {
            Log.i("Eye Gesture Performed", "LOOK_RIGHT: $coordinatesStr")
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_RIGHT

        } else if (yPos > TOP_BOUNDARY && lastLocationY < TOP_BOUNDARY) {
            // above the phone's front facing camera
            Log.i("Eye Gesture Performed", "LOOK_UP: $coordinatesStr")
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_UP

        } else if (yPos < BOTTOM_BOUNDARY && lastLocationY > BOTTOM_BOUNDARY) {
            // below the phone's charging port.
            Log.i("Eye Gesture Performed", "LOOK_DOWN: $coordinatesStr")
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_DOWN
        }

        lastLocationX = xPos.toDouble()
        lastLocationY = yPos.toDouble()

        return EyeGesture.LOOK_FORWARD
    }
}