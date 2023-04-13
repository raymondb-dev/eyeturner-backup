package raymondbdev.eyeturner.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.UserStatusCallback;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment3 extends Fragment {

    private int blinkCount = 0;

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;

    private boolean finished = false;

    private final UserStatusCallback userStatusCallback = new UserStatusCallback() {
        @Override
        public void onAttention(long timestampBegin, long timestampEnd, float score) {}
        @Override
        public void onDrowsiness(long timestamp, boolean isDrowsiness) {}
        @Override
        public void onBlink(long timestamp, boolean isBlinkLeft, boolean isBlinkRight, boolean isBlink, float eyeOpenness) {
            if(isBlink) {
                blinkCount++;
                Log.i("Blink notice:", String.valueOf(blinkCount));
            }

            if(blinkCount >= 3 && !finished) {
                finished = true;

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NavHostFragment.findNavController(TutorialFragment3.this)
                                .navigate(R.id.action_completeTutorial3);
                    }
                });
            }
        }
    };
    private final GazeCallback tutorialGazeCallBack = gazeInfo -> {};

    public TutorialFragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment tutorial_pt1.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment3 newInstance() {
        TutorialFragment3 fragment = new TutorialFragment3();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentViewModel = new ViewModelProvider(requireActivity()).get(ParentViewModel.class);
        gazeTrackerHelper = parentViewModel.getTracker().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gazeTrackerHelper.setUserStatusCallback(userStatusCallback);
        gazeTrackerHelper.setGazeCallback(tutorialGazeCallBack);
        gazeTrackerHelper.initGazeTracker();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial3, container, false);
    }
}