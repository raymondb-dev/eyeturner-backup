package raymondbdev.eyeturner.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.callback.UserStatusCallback
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.GazeTrackerHelper
import raymondbdev.eyeturner.Model.ParentViewModel
import raymondbdev.eyeturner.Model.enums.EyeGesture
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.FragmentTutorial3Binding

/**
 * unused Fragment
 */
class TutorialFragment3 : Fragment() {

    private var binding: FragmentTutorial3Binding? = null

    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null

    private val tutorialGazeCallBack = GazeCallback { gazeInfo: GazeInfo? ->
        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)

        if (gesture == EyeGesture.LOOK_UP) {
            finishTutorial()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
        gazeTrackerHelper = parentViewModel!!.tracker
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gazeTrackerHelper!!.setGazeCallback(tutorialGazeCallBack)
        gazeTrackerHelper!!.startTracking()

        binding = FragmentTutorial3Binding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    private fun finishTutorial() {
        val preferences = requireActivity().getSharedPreferences("settings", 0)

        if(preferences.getBoolean("FirstBoot", true)) {
            Log.i("EyeTurner Log", "User completed tutorial")
            preferences.edit().putBoolean("FirstBoot", false).apply()
        }

        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@TutorialFragment3)
                .navigate(R.id.action_completeTutorial3)
        }
    }

}