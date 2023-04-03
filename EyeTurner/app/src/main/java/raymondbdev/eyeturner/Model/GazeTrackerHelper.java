package raymondbdev.eyeturner.Model;

import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.InitializationCallback;
import camp.visual.gazetracker.callback.UserStatusCallback;
import camp.visual.gazetracker.constant.InitializationErrorType;
import camp.visual.gazetracker.constant.UserStatusOption;

public class GazeTrackerHelper {
    private final String devKey = "dev_9cnwers1nq1kqt1rvxr5na2vfseqer25hlmc0vbh";

    private final WeakReference<Context> mContext;
    private GazeCallback gazeCallback;
    private UserStatusCallback userStatusCallback;
    private GazeTracker gazeTracker;

    private final InitializationCallback initializationCallback = (gazeTracker, error) -> {
        if (gazeTracker != null) {
            successfulInitialization(gazeTracker);
        } else {
            failedInitialization(error);
        }
    };

    public GazeTrackerHelper(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void setUserStatusCallback(UserStatusCallback userStatusCallback) {
        this.userStatusCallback = userStatusCallback;
    }

    public void setGazeCallback(GazeCallback gazeCallback) {
        Log.i("Gazecallback", String.valueOf(gazeCallback));
        this.gazeCallback = gazeCallback;
    }

    public void initGazeTracker() {
        // GazeTracker can react on Blink action.
        UserStatusOption options = new UserStatusOption();
        options.useBlink();

        GazeTracker.initGazeTracker(mContext.get(), devKey, initializationCallback, options);
    }

    private void successfulInitialization(GazeTracker gazeTracker) {

        this.gazeTracker = gazeTracker;

        // TODO: may be persisting user status callback.
        if(userStatusCallback != null) {
            this.gazeTracker.setUserStatusCallback(userStatusCallback);
        }

        Log.i("Gazecallback Start", String.valueOf(gazeCallback));

        this.gazeTracker.setGazeCallback(gazeCallback);
        this.gazeTracker.startTracking();
    }

    private void failedInitialization(InitializationErrorType error) {
        String err = "";
        if (error == InitializationErrorType.ERROR_INIT) {
            // When initialization is failed
            err = "Initialization failed.";
        } else if (error == InitializationErrorType.ERROR_CAMERA_PERMISSION) {
            // When camera permission doesn not exists
            err = "Required Permission was denied.";
        } else  {
            // Gaze library initialization failure
            // It can be caused by several reasons(i.e. Out of memory).
            err = "Gaze Initialization library failed.";
        }
        Log.w("SeeSo", "Initialization Error: " + err);
    }

    public void deinitGazeTracker() {
        GazeTracker.deinitGazeTracker(gazeTracker);
        this.gazeTracker = null;
    }
}