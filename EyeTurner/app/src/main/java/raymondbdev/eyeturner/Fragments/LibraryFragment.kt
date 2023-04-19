package raymondbdev.eyeturner.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.gaze.GazeInfo
import com.github.mertakdut.exception.ReadingException
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import raymondbdev.eyeturner.Adapters.BookLibraryAdapter
import raymondbdev.eyeturner.Model.*
import raymondbdev.eyeturner.Model.Enums.EyeGesture
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.FragmentLibraryBinding


/**
 * Fragment controller for Library (main) page
 */
class LibraryFragment: Fragment(), PickiTCallbacks {

    private var bookGalleryAdapter: BookLibraryAdapter? = null

    private var binding: FragmentLibraryBinding? = null
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var settingsManager: SettingsManager? = null
    private var readerInitialised = false

    var libraryGazeCallback = GazeCallback { gazeInfo: GazeInfo? ->
        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)

//        if (gesture === EyeGesture.LOOK_LEFT) {
//            getPreviousBook()
//        } else if (gesture === EyeGesture.LOOK_RIGHT) {
//            getNextBook()
//        } else
        if (gesture === EyeGesture.LOOK_UP) {
            if (readerInitialised) {
                navigateToFragment(R.id.LibraryToPage_Transition)
            }
        } else if (gesture === EyeGesture.LOOK_DOWN) {
            navigateToFragment(R.id.LibraryToSettings_Transition)
        }
    }

    var openFileResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            Log.i("File Opened", data!!.dataString!!)

            val getPathOperation = PickiT(requireActivity(), this, requireActivity())
            getPathOperation.getPath(data.data, Build.VERSION.SDK_INT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("LIBRARY_FRAGMENT", "Opened")
        parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
        gazeTrackerHelper = parentViewModel!!.tracker
        settingsManager = parentViewModel!!.settingsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        gazeTrackerHelper!!.setGazeCallback(libraryGazeCallback)

        if(gazeTrackerHelper!!.exists()) {
            gazeTrackerHelper!!.startTracking()
        } else {
            // the sole GazeTracker instance is initialised here
            gazeTrackerHelper!!.initGazeTracker()
        }

        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        setupButtons()
        setupGallery()

        return binding!!.getRoot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("LIBRARY_FRAGMENT", "Closed")
        binding = null
    }

    fun navigateToFragment(transitionId: Int) {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@LibraryFragment)
                .navigate(transitionId)
        }
    }

    fun openFilePicker() {
        val selectEBook = Intent(Intent.ACTION_GET_CONTENT)
        selectEBook.type = "application/epub+zip"
        openFileResultLauncher.launch(selectEBook)
    }

    fun setupButtons() {
        // Setup buttons
        binding!!.floatingTutorialButton.setOnClickListener { view ->
            navigateToFragment(R.id.LibraryToTutorial_Transition)
        }

        binding!!.buttonAddFile.setOnClickListener { view ->
            openFilePicker()
        }
    }

    fun setupGallery() {
        // BookLibraryAdapter(requireContext(), db.getListOfBooks())
        bookGalleryAdapter = BookLibraryAdapter(requireContext(), ArrayList())
        binding!!.bookGallery.adapter = bookGalleryAdapter
        binding!!.bookGallery.visibility = GONE
    }

    fun getPreviousBook() {
        val previousBook = bookGalleryAdapter!!.getPreviousItem()

        if(previousBook != null) {
            setBookThumbnail(previousBook.image)
        }

    }

    fun getNextBook() {
        val nextBook = bookGalleryAdapter!!.getNextItem()

        if(nextBook != null) {
            setBookThumbnail(nextBook.image)
        }
    }

    fun setBookThumbnail(bitmap: Bitmap) {
        requireActivity().runOnUiThread {
            binding!!.selectedImage.setImageBitmap(bitmap)
        }
    }

    /**
     * Triggers upon successful file opening. Stores book in database
     */
    override fun PickiTonCompleteListener(
        path: String,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String
    ) {
        // TODO: Handle case when book is already stored
        Log.i("File Successfully Opened", path)

        // retrieving image from selected ebook
        val readingTracker = parentViewModel!!.readingTracker

        readingTracker!!.setBookFromFile(path)
        val imageByteArray = readingTracker.getCoverImage()
        val imageBitmap = ImageConverter.byteToBitmap(imageByteArray)
        setBookThumbnail(imageBitmap!!)

        val storedBook = StoredBook("Book", path, 1, imageBitmap)
        bookGalleryAdapter!!.addItem(storedBook)

        // configuring reader
        val reader = readingTracker.reader
        reader!!.setMaxContentPerSection(settingsManager!!.maxStringSize)
        reader.setIsIncludingTextContent(true)

        readerInitialised = try {
            reader.setFullContent(path)

            // TODO: clean up
            binding!!.buttonSelectBook.visibility = VISIBLE
            binding!!.bookGallery.visibility = VISIBLE
            true
        } catch (e: ReadingException) {
            Toast.makeText(requireContext(), "EPUB File supplied is invalid", Toast.LENGTH_SHORT).show()
            false
        }

    }

    override fun PickiTonUriReturned() {}
    override fun PickiTonStartListener() {}
    override fun PickiTonProgressUpdate(progress: Int) {}
    override fun PickiTonMultipleCompleteListener(paths: ArrayList<String>, wasSuccessful: Boolean, Reason: String) {}
}