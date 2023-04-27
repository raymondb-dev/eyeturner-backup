package raymondbdev.eyeturner.Model

import android.util.Log
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.enums.EyeGesture

/**
 * Contains functions for parsing Eye Gestures.
 */
class EyeGestureParser {
    // Gesture boundaries for the phone (tested on Google Pixel 6 Pro)
    var LEFT_BOUNDARY = -200
    var RIGHT_BOUNDARY = 1700
    var TOP_BOUNDARY = -400
    var BOTTOM_BOUNDARY = 2100

    private var lastLocationX = 0.0
    private var lastLocationY = 0.0

    fun calculateEyeGesture(gazeInfo: GazeInfo): EyeGesture {
        val xPos = gazeInfo.x
        val yPos = gazeInfo.y
        val coordinatesStr = String.format("x - $xPos y - $yPos")
//        Log.i("Eye Boundaries", "LOOK_RIGHT: $coordinatesStr")

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