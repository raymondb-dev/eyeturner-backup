package raymondbdev.eyeturner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.enums.EyeGesture
import raymondbdev.eyeturner.Model.GazeTrackerHelper
import raymondbdev.eyeturner.Model.ParentViewModel
import raymondbdev.eyeturner.Model.SettingsManager
import raymondbdev.eyeturner.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var settingsManager: SettingsManager? = null

    var settingGazeCallback = GazeCallback { gazeInfo: GazeInfo? ->
        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)
        if (gesture == EyeGesture.LOOK_LEFT) {
            previousFontSize()
        } else if (gesture == EyeGesture.LOOK_RIGHT) {
            nextFontSize()
        } else if (gesture == EyeGesture.LOOK_UP) {
            navigateToLibrary()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
        gazeTrackerHelper = parentViewModel!!.tracker
        settingsManager = parentViewModel!!.settingsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gazeTrackerHelper!!.setGazeCallback(settingGazeCallback)
        gazeTrackerHelper!!.startTracking()

        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupButtons();

        return binding!!.getRoot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun setupButtons() {
        // Assign controls to buttons.
        binding!!.fontSizeValue.setText(settingsManager!!.fontSize.toString())

        binding!!.colourModeValue.setText(settingsManager!!.getColourMode())
        binding!!.colourModeValue.setOnClickListener { view ->
            settingsManager!!.switchColourMode()
            binding!!.colourModeValue.setText(settingsManager!!.getColourMode().toString())
        }
    }

    private fun previousFontSize() {
        requireActivity().runOnUiThread {
            binding!!.fontSizeValue.setText(
                settingsManager!!.getPreviousFontSize().toString()
            )
        }
    }

    private fun nextFontSize() {
        requireActivity().runOnUiThread {
            binding!!.fontSizeValue.setText(
                settingsManager!!.getNextFontSize().toString()
            )
        }
    }

    private fun navigateToLibrary() {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@SettingsFragment).popBackStack()
        }
    }
}