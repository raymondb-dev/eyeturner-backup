package raymondbdev.eyeturner.Fragments

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
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.FragmentTutorial2Binding
import raymondbdev.eyeturner.databinding.FragmentTutorial3Binding

class TutorialFragment3 : Fragment() {

    private var binding: FragmentTutorial3Binding? = null

    private var blinkCount = 0
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var finished = false
    private val userStatusCallback: UserStatusCallback = object : UserStatusCallback {
        override fun onAttention(timestampBegin: Long, timestampEnd: Long, score: Float) {}
        override fun onDrowsiness(timestamp: Long, isDrowsiness: Boolean) {}
        override fun onBlink(
            timestamp: Long,
            isBlinkLeft: Boolean,
            isBlinkRight: Boolean,
            isBlink: Boolean,
            eyeOpenness: Float
        ) {
            if (isBlink) {
                Log.i("User Blink Count", blinkCount.toString())
                setAndDisplayBlinkCount()
            }
            if (blinkCount >= 3 && !finished) {
                finishTutorial()
            }
        }
    }

    private val tutorialGazeCallBack = GazeCallback { gazeInfo: GazeInfo? -> }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
        gazeTrackerHelper = parentViewModel!!.tracker
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gazeTrackerHelper!!.setUserStatusCallback(userStatusCallback)
        gazeTrackerHelper!!.setGazeCallback(tutorialGazeCallBack)
        gazeTrackerHelper!!.startTracking()

        binding = FragmentTutorial3Binding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    private fun finishTutorial() {
        finished = true
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@TutorialFragment3)
                .navigate(R.id.action_completeTutorial3)
        }
    }

    private fun setAndDisplayBlinkCount() {
        blinkCount++
        parentViewModel!!.vibrate()

        requireActivity().runOnUiThread {
            binding?.blinkCountText?.setText(
                blinkCount.toString()
            )
        }
    }
}