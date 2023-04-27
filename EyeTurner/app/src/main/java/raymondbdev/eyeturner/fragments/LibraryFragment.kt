package raymondbdev.eyeturner.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import camp.visual.gazetracker.callback.GazeCallback
import camp.visual.gazetracker.gaze.GazeInfo
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import raymondbdev.eyeturner.Model.*
import raymondbdev.eyeturner.Model.enums.EyeGesture
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.adapters.BookCardAdapter
import raymondbdev.eyeturner.databinding.FragmentLibraryBinding


/**
 * Fragment controller for Library (main) page
 */
class LibraryFragment: Fragment(), PickiTCallbacks {

    private var binding: FragmentLibraryBinding? = null
    private var parentViewModel: ParentViewModel? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null
    private var settingsManager: SettingsManager? = null
    private var readingTracker: ReadingTracker? = null
    private var libraryAdapter: BookCardAdapter? = null

    private var position: Int = 0
    private var books: ArrayList<StoredBook>? = null

    private var libraryGazeCallback = GazeCallback { gazeInfo: GazeInfo? ->
        val gesture = gazeTrackerHelper!!.calculateEyeGesture(gazeInfo!!)

        if (gesture === EyeGesture.LOOK_LEFT) {
            getPreviousBook()
        } else if (gesture === EyeGesture.LOOK_RIGHT) {
            getNextBook()
        } else if (gesture === EyeGesture.LOOK_UP) {
            if (books!!.size > 0) {
                navigateToFragment(R.id.LibraryToPage_Transition)
            }
        } else if (gesture === EyeGesture.LOOK_DOWN) {
            navigateToFragment(R.id.LibraryToSettings_Transition)
        }
    }

    private var openFileResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val getPathOperation = PickiT(requireActivity(), this, requireActivity())
            getPathOperation.getPath(data!!.data, Build.VERSION.SDK_INT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
        gazeTrackerHelper = parentViewModel!!.tracker
        settingsManager = parentViewModel!!.settingsManager
        readingTracker = parentViewModel!!.readingTracker
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
        setupCardList()

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun navigateToFragment(transitionId: Int) {
        requireActivity().runOnUiThread {
            gazeTrackerHelper!!.stopTracking()
            NavHostFragment.findNavController(this@LibraryFragment)
                .navigate(transitionId)
        }
    }

    private fun openFilePicker() {
        val selectEBook = Intent(Intent.ACTION_GET_CONTENT)
        selectEBook.type = "application/epub+zip"
        openFileResultLauncher.launch(selectEBook)
    }

    private fun setupButtons() {
        // Setup buttons

        binding!!.floatingTutorialButton.setOnClickListener { view ->
            navigateToFragment(R.id.LibraryToTutorial_Transition)
        }

        binding!!.buttonAddFile.setOnClickListener { view ->
            openFilePicker()
        }

        binding!!.clearLibraryButton.setOnClickListener { view ->
            clearLibrary()
        }
    }

    private fun setupCardList() {
        books = readingTracker!!.getBooksFromDB()

        // sets up default book
        if(books!!.isEmpty()) {
            binding!!.buttonSelectBook.visibility = INVISIBLE
        } else {
            // we set the most recent book as the default
            setSelectedBook(0)
        }

        val horizontalLayout = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = BookCardAdapter(requireContext(), books!!)

        libraryAdapter = adapter
        binding!!.libraryDisplay.layoutManager = horizontalLayout
        binding!!.libraryDisplay.adapter = adapter
    }

    private fun getPreviousBook() {
        if(position > 0) {
            position--
            setSelectedBook(position)
        }
    }

    private fun getNextBook() {
        val count = libraryAdapter!!.itemCount
        if(position < count - 1) {
            position++
            setSelectedBook(position)
        }
    }

    private fun setSelectedBook(arrayPosition: Int) {

        val book = books!![arrayPosition]
        readingTracker!!.bookInfo = book
        readingTracker!!.currentPageIndex = book.pageNumber
        readingTracker!!.configureReader(book.bookPath, settingsManager!!.maxStringSize)

        requireActivity().runOnUiThread {
            binding!!.buttonSelectBook.visibility = VISIBLE
            binding!!.selectedImage.setImageBitmap(book.image)
            binding!!.currentBookText.text = book.bookName
            binding!!.libraryDisplay.smoothScrollToPosition(position)
        }
    }

    private fun clearLibrary() {
        readingTracker!!.clearDB()

        // resets to initial state.
        setupCardList()
        binding!!.buttonSelectBook.visibility = GONE
        binding!!.selectedImage.setImageResource(R.drawable.youhavenobooks)
        binding!!.currentBookText.text = ""
    }

    /**
     * Triggers upon successful file opening. Stores book in database
     */
    override fun PickiTonCompleteListener(
        bookPath: String,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String
    ) {
        // TODO: Handle case when book is already stored
        Log.i("File Successfully Opened", bookPath)

        // retrieving metadata from book
        val bookName = readingTracker!!.setBookFromFile(bookPath)
        val bookThumbnail = readingTracker!!.getCoverImage()!!

        // add books to library and set thumbnail
        if(readingTracker!!.bookExistsDB(bookName)) {
            readingTracker!!.bookInfo = readingTracker!!.getBook(bookName)!!
            return
        }

        // adds book to library and configures reader
        readingTracker!!.addBookToDB(bookName, bookPath, bookThumbnail, System.currentTimeMillis())
        readingTracker!!.bookInfo = readingTracker!!.getBook(bookName)
        setupCardList()

        // Reader is configured.
        if(readingTracker!!.configureReader(bookPath, settingsManager!!.maxStringSize)) {
            setSelectedBook(0)
        } else {
            Toast.makeText(requireContext(), "EPUB File supplied is invalid", Toast.LENGTH_SHORT).show()
        }

        Log.i("COMPLETED", "PROCESSING BOOK")

        // onCreate(this)
    }

    override fun PickiTonUriReturned() {}
    override fun PickiTonStartListener() {}
    override fun PickiTonProgressUpdate(progress: Int) {}
    override fun PickiTonMultipleCompleteListener(paths: ArrayList<String>, wasSuccessful: Boolean, Reason: String) {}
}