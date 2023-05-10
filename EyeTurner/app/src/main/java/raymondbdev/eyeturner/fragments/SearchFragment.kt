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
import raymondbdev.eyeturner.databinding.FragmentSearchBinding

/**
 * Unused Fragment (may implement later)
 */
class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null

    var settingGazeCallback = GazeCallback { gazeInfo: GazeInfo? ->
        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)
        if (gesture == EyeGesture.LOOK_UP) {
            navigateToLibrary()
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
        gazeTrackerHelper!!.setGazeCallback(settingGazeCallback)
        gazeTrackerHelper!!.startTracking()

        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun navigateToLibrary() {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@SearchFragment).popBackStack()
        }
    }
}