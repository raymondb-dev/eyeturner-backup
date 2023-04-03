package raymondbdev.eyeturner.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mertakdut.Reader;

public class ParentViewModel extends ViewModel {
    private final MutableLiveData<GazeTrackerHelper> mutableGazeTrackerHelper =
            new MutableLiveData<>();



    public void setTracker(GazeTrackerHelper item) {
        mutableGazeTrackerHelper.setValue(item);
    }

    public LiveData<GazeTrackerHelper> getTracker() {
        return mutableGazeTrackerHelper;
    }

    //    private final MutableLiveData<Integer> mutablePageIndex = new MutableLiveData<>();
//    private final MutableLiveData<Reader> mutableBookReader = new MutableLiveData<>();

//    public void setBookReader(Reader item) {
//        mutableBookReader.setValue(item);
//    }
//
//    public LiveData<Reader> getBookReader() {
//        return mutableBookReader;
//    }
//
//    public void setPageIndex(Integer item) {
//        mutablePageIndex.setValue(item);
//    }
//
//    public LiveData<Integer> getPageIndex() {
//        return mutablePageIndex;
//    }
}
