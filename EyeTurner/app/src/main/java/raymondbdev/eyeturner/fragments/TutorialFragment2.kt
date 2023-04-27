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
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.FragmentTutorial2Binding

class TutorialFragment2 : Fragment() {

    private var binding: FragmentTutorial2Binding? = null
    private var lookUpCount = 0
    private var lookDownCount = 0
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var finished = false

    var tutorialGazeCallBack2 = GazeCallback { gazeInfo: GazeInfo? ->

        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)

        if (gesture == EyeGesture.LOOK_UP) {
            setAndDisplayUpTextbox()
        } else if (gesture == EyeGesture.LOOK_DOWN) {
            setAndDisplayDownTextbox()
        }

        if ((lookUpCount >= 3) && (lookDownCount >= 3) && !finished) {
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
        gazeTrackerHelper!!.setGazeCallback(tutorialGazeCallBack2)
        gazeTrackerHelper!!.startTracking()

        // Inflate fragment's layout
        binding = FragmentTutorial2Binding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun finishTutorial() {
        finished = true
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            // NavHostFragment.findNavController(this@TutorialFragment2)
            //     .navigate(R.id.action_completeTutorial2)
            NavHostFragment.findNavController(this@TutorialFragment2)
                .navigate(R.id.action_Tutorial2Home)
        }
    }

    private fun setAndDisplayUpTextbox() {
        lookUpCount++
        parentViewModel!!.vibrate()

        requireActivity().runOnUiThread {
            binding?.upCountText?.setText(
                lookUpCount.toString()
            )
        }
    }

    private fun setAndDisplayDownTextbox() {
        lookDownCount++
        parentViewModel!!.vibrate()

        requireActivity().runOnUiThread {
            binding?.downCountText?.setText(
                lookDownCount.toString()
            )
        }
    }
}