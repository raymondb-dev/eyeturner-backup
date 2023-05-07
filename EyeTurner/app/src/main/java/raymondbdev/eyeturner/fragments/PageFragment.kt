package raymondbdev.eyeturner.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.bold
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.gaze.GazeInfo
import raymondbdev.eyeturner.Model.GazeTrackerHelper
import raymondbdev.eyeturner.Model.ParentViewModel
import raymondbdev.eyeturner.Model.ReadingTracker
import raymondbdev.eyeturner.Model.SettingsManager
import raymondbdev.eyeturner.Model.enums.EyeGesture
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.FragmentPageBinding
import java.util.regex.Pattern


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
        /* else if (gesture == EyeGesture.LOOK_DOWN) {
            navigateToSettings()
        } */
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

        val instruction = "Look Left and Right to turn the page."
        Toast.makeText(requireContext(), instruction, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        gazeTrackerHelper!!.setGazeCallback(readingCallback)
        gazeTrackerHelper!!.startTracking()

        // updateFontSize()

        // Inflate the layout for this fragment
        binding = FragmentPageBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set page content with font size
        binding!!.pageContentText.textSize = settingsManager!!.fontSize.toFloat()
        binding!!.pageContentText.text = readingTracker!!.getCurrentPage()
        setPageNumber(readingTracker!!.currentPageIndex)
    }

    private fun navigateToLibrary() {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@PageFragment).popBackStack()
        }
    }

    private fun navigateToSettings() {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@PageFragment)
                .navigate(R.id.PageToSettings_Transition)
        }
    }

    private fun updateFontSize() {
        readingTracker!!.updateFontSize(
            settingsManager!!.fontSize,
            settingsManager!!.maxStringSize)

    }

    /**
     * Increments the page index and moves to the next page.
     */
    private fun nextPage() {
        requireActivity().runOnUiThread {
            setPageContent(readingTracker!!.turnPageRight())
            setPageNumber(readingTracker!!.currentPageIndex)
        }

    }

    /**
     * Decrements the page index and moves to the previous page.
     */
    private fun previousPage() {
        requireActivity().runOnUiThread {
            setPageContent(readingTracker!!.turnPageLeft())
            setPageNumber(readingTracker!!.currentPageIndex)
        }

    }

    private fun setPageContent(pageContent: String) {

        val modifiedPageContent = SpannableStringBuilder()

        // Find instances of "Chapter x" and makes it bold.
        val chapterPattern = Pattern.compile("(Chapter (\\d+|\\w+))") // the pattern to search for
        val chapterMatcher = chapterPattern.matcher(pageContent)

        if(chapterMatcher.find()) {
            val chapterSplit = pageContent.split(chapterMatcher.group(1)!!)
            modifiedPageContent.append(chapterSplit[0])
            modifiedPageContent.bold{ append(chapterMatcher.group(1)!!) }
            modifiedPageContent.append(chapterSplit[1])
        } else {
            modifiedPageContent.append(pageContent)
        }

        binding!!.pageContentText.text = modifiedPageContent
    }

    private fun setPageNumber(pageNumber: Int) {

        Log.i("Page Number", pageNumber.toString())

        // TODO: use actual font
        // val defaultTypeface = Typeface.createFromAsset(requireActivity().assets, "font/literata.ttf")
        val defaultTypeface = null

        if(pageNumber == 1) { // User on Title Page
            binding!!.pageContentText.textSize = 28F
            binding!!.pageContentText.setTypeface(defaultTypeface, Typeface.BOLD)
        } else {
            binding!!.pageContentText.textSize = settingsManager!!.fontSize.toFloat()
            binding!!.pageContentText.setTypeface(defaultTypeface, Typeface.NORMAL)
        }

        binding!!.pageNumberText.text = pageNumber.toString()
    }

    /**
     * Navigates to library page
     */
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