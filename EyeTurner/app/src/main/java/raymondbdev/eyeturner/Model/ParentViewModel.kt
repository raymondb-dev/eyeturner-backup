package raymondbdev.eyeturner.Model

import android.content.ContentResolver
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Wrapper Class which allows us to access persistive objects between fragments.
 */
class ParentViewModel : ViewModel() {
    private val mutableGazeTrackerHelper = MutableLiveData<GazeTrackerHelper>()
    private val mutableSettingsManager = MutableLiveData<SettingsManager>()
    private val mutableReadingTracker = MutableLiveData<ReadingTracker>()
    private val mutableVibrator = MutableLiveData<Vibrator>()
    private val mutableContentResolver = MutableLiveData<ContentResolver>()

    val tracker: GazeTrackerHelper?
        get() = mutableGazeTrackerHelper.value

    val settingsManager: SettingsManager?
        get() = mutableSettingsManager.value

    val readingTracker: ReadingTracker?
        get() = mutableReadingTracker.value

    fun setTracker(helper: GazeTrackerHelper) {
        mutableGazeTrackerHelper.value = helper
    }

    fun setSettingsManager(manager: SettingsManager) {
        mutableSettingsManager.value = manager
    }

    fun setVibrator(vibrator: Vibrator) {
        mutableVibrator.value = vibrator
    }

    fun setMutableContentResolver(contentResolver: ContentResolver) {
        mutableContentResolver.value = contentResolver
    }

    fun setReadingHelper(helper: ReadingTracker) {
        mutableReadingTracker.value = helper
    }

    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // vibrate API only available
            mutableVibrator.value!!.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }

    fun getMutableContentResolver(): ContentResolver? {
        return mutableContentResolver.value
    }


}
