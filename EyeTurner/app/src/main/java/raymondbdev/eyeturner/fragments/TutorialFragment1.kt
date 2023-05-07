package raymondbdev.eyeturner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.enums.EyeGesture
import raymondbdev.eyeturner.Model.GazeTrackerHelper
import raymondbdev.eyeturner.Model.ParentViewModel
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.FragmentTutorial1Binding

class TutorialFragment1 : Fragment() {

    private var binding: FragmentTutorial1Binding? = null
    private var lookLeftCount = 0
    private var lookRightCount = 0
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var finished = false

    var tutorialGazeCallBack1 = GazeCallback { gazeInfo: GazeInfo? ->

        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)

        if (gesture == EyeGesture.LOOK_LEFT) {
            setAndDisplayLeftTextbox()
        } else if (gesture == EyeGesture.LOOK_RIGHT) {
            setAndDisplayRightTextbox()
        }

        if ((lookLeftCount >= 3) && (lookRightCount >= 3) && !finished) {
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
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentTutorial1Binding.inflate(inflater, container, false)
        setupTracker()

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun setupTracker() {
        gazeTrackerHelper!!.setGazeCallback(tutorialGazeCallBack1)

        if(gazeTrackerHelper!!.exists()) {
            gazeTrackerHelper!!.startTracking()
        } else {
            // the sole GazeTracker instance is initialised here
            gazeTrackerHelper!!.initGazeTracker()
            Toast.makeText(requireContext(), "Loading Eye Tracking", Toast.LENGTH_SHORT).show()
        }
    }

    fun finishTutorial() {
        finished = true
        gazeTrackerHelper!!.stopTracking()
        requireActivity().runOnUiThread {
            NavHostFragment.findNavController(this@TutorialFragment1)
                .navigate(R.id.action_completeTutorial1)
        }
    }

    private fun setAndDisplayLeftTextbox() {
        lookLeftCount++
        parentViewModel!!.vibrate()

        requireActivity().runOnUiThread {
            binding?.leftCountText?.setText(
                lookLeftCount.toString()
            )
        }
    }

    private fun setAndDisplayRightTextbox() {
        lookRightCount++
        parentViewModel!!.vibrate()

        requireActivity().runOnUiThread {
            binding?.rightCountText?.setText(
                lookRightCount.toString()
            )
        }
    }
}