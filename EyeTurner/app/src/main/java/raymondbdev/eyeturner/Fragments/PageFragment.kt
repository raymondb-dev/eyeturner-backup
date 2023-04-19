package raymondbdev.eyeturner.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.*
import raymondbdev.eyeturner.Model.Enums.EyeGesture
import raymondbdev.eyeturner.databinding.FragmentPageBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PageFragment : Fragment() {

    private var binding: FragmentPageBinding? = null

    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var settingsManager: SettingsManager? = null
    private var readingTracker: ReadingTracker? = null

    private var mPageContents: String? = null

    var readingCallback = GazeCallback { gazeInfo: GazeInfo? ->
        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)
        if (gesture == EyeGesture.LOOK_LEFT) { // turns page left.
            previousPage()
        } else if (gesture == EyeGesture.LOOK_RIGHT) { // turns page right.
            nextPage()
        } else if (gesture == EyeGesture.LOOK_UP) { // navigates to library.
            navigateToLibrary()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mPageContents = requireArguments().getString(ARG_PAGE_CONTENTS)
        }

        parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
        gazeTrackerHelper = parentViewModel!!.tracker
        settingsManager = parentViewModel!!.settingsManager
        readingTracker = parentViewModel!!.readingTracker
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        gazeTrackerHelper!!.setGazeCallback(readingCallback)
        gazeTrackerHelper!!.startTracking()

        // Inflate the layout for this fragment
        binding = FragmentPageBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set page content with font size
        binding!!.pageContentText.textSize = settingsManager!!.fontSize.toFloat()
        binding!!.pageContentText.text = readingTracker!!.getCurrentPage()
        binding!!.pageNumberText.text = readingTracker!!.currentPageIndex.toString()
    }

    /**
     * Increments the page index and moves to the next page.
     */
    fun nextPage() {
        requireActivity().runOnUiThread {
            binding!!.pageContentText.text = readingTracker!!.turnPageRight()
            binding!!.pageNumberText.text = readingTracker!!.currentPageIndex.toString()
        }
    }

    /**
     * Decrements the page index and moves to the previous page.
     */
    fun previousPage() {
        requireActivity().runOnUiThread {
            binding!!.pageContentText.text = readingTracker!!.turnPageLeft()
            binding!!.pageNumberText.text = readingTracker!!.currentPageIndex.toString()
        }
    }

    /**
     * Navigates to library page
     */
    private fun navigateToLibrary() {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@PageFragment).popBackStack()
        }
    }

    companion object {
        private const val ARG_PAGE_CONTENTS = "PageContents"
        fun newInstance(pageContents: String?): PageFragment {
            val fragment = PageFragment()
            val args = Bundle()
            args.putString(ARG_PAGE_CONTENTS, pageContents)
            fragment.arguments = args
            return fragment
        }
    }

}