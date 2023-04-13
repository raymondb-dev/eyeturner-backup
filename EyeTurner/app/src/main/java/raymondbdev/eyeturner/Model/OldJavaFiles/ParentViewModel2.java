package raymondbdev.eyeturner.Model.OldJavaFiles;

import android.content.ContentResolver;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mertakdut.Reader;

import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.SettingsManager;

/**
 * Wrapper Class which allows us to access persistive objects between fragments.
 */
public class ParentViewModel2 extends ViewModel {

    private final MutableLiveData<GazeTrackerHelper> mutableGazeTrackerHelper = new MutableLiveData<>();
    private final MutableLiveData<SettingsManager> mutableSettingsManager = new MutableLiveData<>();
    private final MutableLiveData<Reader> mutableBookReader = new MutableLiveData<>();
    private final MutableLiveData<Vibrator> mutableVibrator = new MutableLiveData<>();
    private final MutableLiveData<ContentResolver> mutableContentResolver = new MutableLiveData<>();

    public void setTracker(GazeTrackerHelper helper) {
        mutableGazeTrackerHelper.setValue(helper);
    }

    public LiveData<GazeTrackerHelper> getTracker() {
        return mutableGazeTrackerHelper;
    }

    public void setSettingsManager(SettingsManager manager) {
        mutableSettingsManager.setValue(manager);
    }

    public LiveData<SettingsManager> getSettingsManager() {
        return mutableSettingsManager;
    }

    public void setVibrator(Vibrator vibrator) {
        mutableVibrator.setValue(vibrator);
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mutableVibrator.getValue().vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    public void setBookReader(Reader item) {
        mutableBookReader.setValue(item);
    }

    public Reader getBookReader() {
        return mutableBookReader.getValue();
    }

    public ContentResolver getMutableContentResolver() {
        return mutableContentResolver.getValue();
    }

    public void setMutableContentResolver(ContentResolver contentResolver) {
        mutableContentResolver.setValue(contentResolver);
    }
}
