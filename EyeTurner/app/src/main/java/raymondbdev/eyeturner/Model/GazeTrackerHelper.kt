package raymondbdev.eyeturner.Model

import android.content.Context
import android.util.Log
import camp.visual.gazetracker.GazeTracker
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.InitializationCallback
import camp.visual.gazetracker.callback.UserStatusCallback
import camp.visual.gazetracker.constant.InitializationErrorType
import java.lang.ref.WeakReference

class GazeTrackerHelper(context: Context) {
    private val devKey = "dev_9cnwers1nq1kqt1rvxr5na2vfseqer25hlmc0vbh"
    private val mContext: WeakReference<Context>
    private var gazeCallback: GazeCallback? = null
    private var userStatusCallback: UserStatusCallback? = null
    private var gazeTracker: GazeTracker? = null
    private val initializationCallback =
        InitializationCallback { gazeTracker: GazeTracker?, error: InitializationErrorType ->
            if (gazeTracker != null) {
                successfulInitialization(gazeTracker)
            } else {
                failedInitialization(error)
            }
        }

    // TODO: Add

    init {
        mContext = WeakReference(context)
    }

    fun setUserStatusCallback(userStatusCallback: UserStatusCallback?) {
        this.userStatusCallback = userStatusCallback
    }

    fun setGazeCallback(gazeCallback: GazeCallback?) {
        this.gazeCallback = gazeCallback
    }

    fun initGazeTracker() {
        // GazeTracker can react on Blink action.
        // UserStatusOption options = new UserStatusOption();
        // options.useBlink();
        // GazeTracker.initGazeTracker(mContext.get(), devKey, initializationCallback, options);
        GazeTracker.initGazeTracker(mContext.get(), devKey, initializationCallback)
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
        Log.w("SeeSo Error Type: ", error.toString())
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
        initGazeTracker()
    }

    fun deinitGazeTracker() {
        GazeTracker.deinitGazeTracker(gazeTracker)
        gazeTracker = null
    }
}