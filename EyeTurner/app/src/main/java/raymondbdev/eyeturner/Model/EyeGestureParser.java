package raymondbdev.eyeturner.Model;

import android.util.Log;

import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.ScreenState;

public class EyeGestureParser {
    /**
     * Calculates what eye gesture is being performed based on eye position.
     * @param gazeInfo GazeInfo Object
     * @return the type of Eye Gesture detected
     */

    private EyeGesture lastGesture = EyeGesture.LOOK_FORWARD;
    private Double lastLocationX = 0.0;
    private Double lastLocationY = 0.0;

    public EyeGestureParser() {}

    public EyeGesture calculateEyeGesture(GazeInfo gazeInfo) {
        float xPos = gazeInfo.x;
        float yPos = gazeInfo.y;

        /* if(lastGesture == EyeGesture.LOOK_FORWARD) {

            if(xPos < 0 && eyeLocation == ScreenState.OUTSIDE_OF_SCREEN) {
                lastGesture = EyeGesture.LOOK_LEFT;
            } else if(xPos > 1500 && eyeLocation == ScreenState.OUTSIDE_OF_SCREEN) {
                lastGesture = EyeGesture.LOOK_RIGHT;
            } else if(yPos < 0 && eyeLocation == ScreenState.OUTSIDE_OF_SCREEN) {
                // above the phone's front facing camera
                lastGesture = EyeGesture.LOOK_UP;
            } else if(yPos > 1700) {
                // below the phone's charging port.
                lastGesture = EyeGesture.LOOK_DOWN;
            }

            if(lastGesture != EyeGesture.LOOK_FORWARD) {
                return lastGesture;
            }

        } */

        if(xPos > 0 && lastLocationX < 0) {
            lastLocationX = (double) xPos;
            lastLocationY = (double) yPos;
            return EyeGesture.LOOK_LEFT;
        } else if(xPos < 1500 && lastLocationX > 1500) {
            lastLocationX = (double) xPos;
            lastLocationY = (double) yPos;
            return EyeGesture.LOOK_RIGHT;
        } else if(yPos > 0 && lastLocationY < 0) {
            // above the phone's front facing camera
            lastLocationX = (double) xPos;
            lastLocationY = (double) yPos;
            return EyeGesture.LOOK_UP;
        } else if(yPos < 1700 && lastLocationY > 1700) {
            // below the phone's charging port.
            lastLocationX = (double) xPos;
            lastLocationY = (double) yPos;
            return EyeGesture.LOOK_DOWN;
        }

        lastLocationX = (double) xPos;
        lastLocationY = (double) yPos;

        return EyeGesture.LOOK_FORWARD;
    }
}
