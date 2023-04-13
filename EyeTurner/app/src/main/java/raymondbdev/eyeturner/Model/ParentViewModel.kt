package raymondbdev.eyeturner.Model

import android.content.ContentResolver
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mertakdut.Reader

/**
 * Wrapper Class which allows us to access persistive objects between fragments.
 */
class ParentViewModel : ViewModel() {
    private val mutableGazeTrackerHelper = MutableLiveData<GazeTrackerHelper>()
    private val mutableSettingsManager = MutableLiveData<SettingsManager>()
    private val mutableBookReader = MutableLiveData<Reader>()
    private val mutableVibrator = MutableLiveData<Vibrator>()
    private val mutableContentResolver = MutableLiveData<ContentResolver>()

    fun setTracker(helper: GazeTrackerHelper) {
        mutableGazeTrackerHelper.value = helper
    }

    val tracker: LiveData<GazeTrackerHelper>
        get() = mutableGazeTrackerHelper

    fun setSettingsManager(manager: SettingsManager) {
        mutableSettingsManager.value = manager
    }

    val settingsManager: LiveData<SettingsManager>
        get() = mutableSettingsManager

    fun setVibrator(vibrator: Vibrator) {
        mutableVibrator.value = vibrator
    }

    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // vibrate API only available
            mutableVibrator.value!!.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }

    fun setBookReader(item: Reader) {
        mutableBookReader.value = item
    }

    val bookReader: Reader?
        get() = mutableBookReader.value

    fun getMutableContentResolver(): ContentResolver? {
        return mutableContentResolver.value
    }

    fun setMutableContentResolver(contentResolver: ContentResolver) {
        mutableContentResolver.value = contentResolver
    }
}
