package raymondbdev.eyeturner.Model

import android.content.Context
import android.util.Log
import camp.visual.gazetracker.GazeTracker
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.InitializationCallback
import camp.visual.gazetracker.callback.UserStatusCallback
import camp.visual.gazetracker.constant.InitializationErrorType
import camp.visual.gazetracker.constant.UserStatusOption
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.enums.EyeGesture
import java.lang.ref.WeakReference

class GazeTrackerHelper(context: Context) {
    private val devKey = "dev_e6fixnnc0ecu5dlchqtm318pr5h45h3s8cgsg4ew"
    private val mContext: WeakReference<Context>
    private var gazeCallback: GazeCallback? = null
    private var userStatusCallback: UserStatusCallback? = null
    private var gazeTracker: GazeTracker? = null
    private var eyeGestureParser = EyeGestureParser()

    private var touchMode: Boolean = false

    private val initializationCallback =
        InitializationCallback { gazeTracker: GazeTracker?, error: InitializationErrorType ->
            if (gazeTracker != null) {
                successfulInitialization(gazeTracker)
            } else {
                failedInitialization(error)
            }
        }

    init {
        mContext = WeakReference(context)
    }

    fun calculateEyeGesture(gazeInfo: GazeInfo): EyeGesture {
        return eyeGestureParser.calculateEyeGesture(gazeInfo)
    }

    fun setUserStatusCallback(userStatusCallback: UserStatusCallback?) {
        this.userStatusCallback = userStatusCallback
    }

    fun setGazeCallback(gazeCallback: GazeCallback?) {
        this.gazeCallback = gazeCallback
    }

    fun initGazeTracker() {
        // GazeTracker can react on Blink action.
         val options = UserStatusOption();
         options.useBlink();

        if(!touchMode) {
            GazeTracker.initGazeTracker(mContext.get(), devKey, initializationCallback, options)
        }

    }

    private fun successfulInitialization(gazeTracker: GazeTracker) {
        this.gazeTracker = gazeTracker

        // TODO: may be persisting user status callback.
        if (userStatusCallback != null) {
            this.gazeTracker!!.setUserStatusCallback(userStatusCallback)
        }
        this.gazeTracker!!.setGazeCallback(gazeCallback)
        this.gazeTracker!!.startTracking()
    }

    private fun failedInitialization(error: InitializationErrorType) {

        var err = ""

        err = if (error == InitializationErrorType.ERROR_INIT) {
            // When initialization is failed
            "Initialization failed."
        } else if (error == InitializationErrorType.ERROR_CAMERA_PERMISSION) {
            // When camera permission doesn not exists
            "Required Permission was denied."
        } else {
            // Gaze library initialization failure
            // It can be caused by several reasons(i.e. Out of memory).
            "Gaze Initialization library failed."
        }

        Log.w("SeeSo", "Initialization Error: $err")
    }

    fun deinitGazeTracker() {
        if(!touchMode && gazeTracker != null) {
            GazeTracker.deinitGazeTracker(gazeTracker)
            gazeTracker = null
        }
    }

    fun exists(): Boolean {
        if(gazeTracker != null) {
            return true
        }

        return false
    }

    fun stopTracking() {
        gazeTracker?.stopTracking()
    }

    fun startTracking() {
        this.gazeTracker!!.setGazeCallback(gazeCallback)
        this.gazeTracker!!.startTracking()
    }
}