package raymondbdev.eyeturner.Model

import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.Enums.EyeGesture

class EyeGestureParser {
    private var lastLocationX = 0.0
    private var lastLocationY = 0.0

    fun calculateEyeGesture(gazeInfo: GazeInfo): EyeGesture? {
        val xPos = gazeInfo.x
        val yPos = gazeInfo.y

        if (xPos > 0 && lastLocationX < 0) {
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_LEFT

        } else if (xPos < 1500 && lastLocationX > 1500) {
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_RIGHT

        } else if (yPos > 0 && lastLocationY < 0) {
            // above the phone's front facing camera
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_UP

        } else if (yPos < 1700 && lastLocationY > 1700) {
            // below the phone's charging port.
            lastLocationX = xPos.toDouble()
            lastLocationY = yPos.toDouble()
            return EyeGesture.LOOK_DOWN
            
        }

        lastLocationX = xPos.toDouble()
        lastLocationY = yPos.toDouble()
        return EyeGesture.LOOK_FORWARD
    }
}