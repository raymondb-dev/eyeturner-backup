package raymondbdev.eyeturner.UnusedJavaFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import camp.visual.gazetracker.callback.GazeCallback;
import raymondbdev.eyeturner.Model.Enums.EyeGesture;
import raymondbdev.eyeturner.Model.EyeGestureParser;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.R;
import raymondbdev.eyeturner.databinding.FragmentTutorial2Binding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment22#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment22 extends Fragment {

    private FragmentTutorial2Binding binding;

    private int lookUpCount = 0;
    private int lookDownCount = 0;

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;
    private EyeGestureParser eyeGestureParser;

    private boolean finished = false;

    GazeCallback tutorialGazeCallBack2 = gazeInfo -> {
        EyeGesture gesture = eyeGestureParser.calculateEyeGesture(gazeInfo);

        if(gesture == EyeGesture.LOOK_UP) {
            Log.i("Eye Gesture Performed:", "Looked Up");
            lookUpCount++;
            requireActivity().runOnUiThread(() -> binding.upCountText.setText(String.valueOf(lookUpCount)));
        } else if (gesture == EyeGesture.LOOK_DOWN) {
            Log.i("Eye Gesture Performed:", "Looked Down");
            lookDownCount++;
            requireActivity().runOnUiThread(() -> binding.downCountText.setText(String.valueOf(lookDownCount)));
        }

        if(lookUpCount >= 3 && lookDownCount >= 3 && !finished) {
            finished = true;

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gazeTrackerHelper.deinitGazeTracker();

                    // TODO:
                    NavHostFragment.findNavController(TutorialFragment22.this)
                            .navigate(R.id.action_tutorial2_to_FirstFragment);
                }
            });
        }
    };

    public TutorialFragment22() {
        // Required empty public constructor
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

        gazeTrackerHelper.setGazeCallback(tutorialGazeCallBack2);
        gazeTrackerHelper.initGazeTracker();

        // Inflate fragment's layout
        binding = FragmentTutorial2Binding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}