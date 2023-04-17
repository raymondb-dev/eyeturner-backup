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
import raymondbdev.eyeturner.Model.Enums.EyeGesture;
import raymondbdev.eyeturner.Model.EyeGestureParser;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.Model.SettingsManager;
import raymondbdev.eyeturner.R;
import raymondbdev.eyeturner.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;
    private EyeGestureParser eyeGestureParser;
    private SettingsManager settingsManager;

    private boolean finished = false;

    GazeCallback settingGazeCallback = gazeInfo -> {
        EyeGesture gesture = eyeGestureParser.calculateEyeGesture(gazeInfo);

        if(gesture == EyeGesture.LOOK_UP) {
            Log.i("Eye Gesture Performed:", "Looked Up");
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gazeTrackerHelper.deinitGazeTracker();
                    NavHostFragment.findNavController(SettingsFragment.this)
                            .navigate(R.id.SettingsToLibrary_Transition);
                }
            });

        }
    };

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eyeGestureParser = new EyeGestureParser();
        parentViewModel = new ViewModelProvider(requireActivity()).get(ParentViewModel.class);
        gazeTrackerHelper = parentViewModel.getTracker().getValue();
        settingsManager = parentViewModel.getSettingsManager().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gazeTrackerHelper.setGazeCallback(settingGazeCallback);
        gazeTrackerHelper.initGazeTracker();

        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        binding.fontSizeValue.setText(settingsManager.getFontSize());

        binding.fontSizeValue.setOnClickListener(view ->
                binding.fontSizeValue.setText(settingsManager.getNextFontSize())
        );

        binding.colourModeValue.setText(settingsManager.getColourMode());
        binding.colourModeValue.setOnClickListener(view -> {
            settingsManager.switchColourMode();
            binding.colourModeValue.setText(settingsManager.getColourMode());
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}