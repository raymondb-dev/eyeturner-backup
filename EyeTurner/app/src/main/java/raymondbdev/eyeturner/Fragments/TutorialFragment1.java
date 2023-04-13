package raymondbdev.eyeturner.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import camp.visual.gazetracker.callback.GazeCallback;
import raymondbdev.eyeturner.Model.Enums.EyeGesture;
import raymondbdev.eyeturner.Model.EyeGestureParser;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.R;
import raymondbdev.eyeturner.databinding.FragmentTutorial1Binding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */

//https://www.tutorialspoint.com/how-to-implement-propertychangelistener-using-lambda-expression-in-java
public class TutorialFragment1 extends Fragment {

    private FragmentTutorial1Binding binding;

    private int lookLeftCount = 0;
    private int lookRightCount = 0;

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;
    private EyeGestureParser eyeGestureParser;

    private boolean finished = false;

    GazeCallback tutorialGazeCallBack1 = gazeInfo -> {
        EyeGesture gesture = eyeGestureParser.calculateEyeGesture(gazeInfo);

        if(gesture == EyeGesture.LOOK_LEFT) {
            Log.i("Eye Gesture Performed:", "Looked Left");
            lookLeftCount++;
            requireActivity().runOnUiThread(() -> binding.leftCountText.setText(String.valueOf(lookLeftCount)));
            parentViewModel.vibrate();

        } else if (gesture == EyeGesture.LOOK_RIGHT) {
            Log.i("Eye Gesture Performed:", "Looked Right");
            lookRightCount++;
            requireActivity().runOnUiThread(() -> binding.rightCountText.setText(String.valueOf(lookRightCount)));
            parentViewModel.vibrate();

        }

        if(lookLeftCount >= 3 && lookRightCount >= 3 && !finished) {
            finished = true;
            gazeTrackerHelper.deinitGazeTracker();

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NavHostFragment.findNavController(TutorialFragment1.this)
                            .navigate(R.id.action_completeTutorial1);
                }
            });
        }
    };

    public TutorialFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment tutorial_pt1.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment1 newInstance() {
        TutorialFragment1 fragment = new TutorialFragment1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eyeGestureParser = new EyeGestureParser();
        parentViewModel = new ViewModelProvider(requireActivity()).get(ParentViewModel.class);
        gazeTrackerHelper = parentViewModel.getTracker().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gazeTrackerHelper.setGazeCallback(tutorialGazeCallBack1);
        gazeTrackerHelper.initGazeTracker();

        // Inflate the layout for this fragment
        binding = FragmentTutorial1Binding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}