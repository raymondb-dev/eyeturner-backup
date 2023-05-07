package raymondbdev.eyeturner.Model

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.github.mertakdut.Reader
import com.github.mertakdut.exception.OutOfPagesException
import com.github.mertakdut.exception.ReadingException
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream

class ReadingTracker(newReader: Reader, newDb: LibraryDBHelper) {
    var currentPageIndex: Int = 1
    var db: LibraryDBHelper? = null

    var bookMetadata: Book? = null
    var bookContent: Reader? = null
    var bookInfo: StoredBook? = null
        set(value) {
            field = value
            currentPageIndex = field!!.pageNumber
        }

    init {
        bookContent = newReader
        db = newDb
    }

    /**
     * Sets books in
     */
    fun setBookFromFile(path: String): String {
        val epubReader = EpubReader()
        bookMetadata = epubReader.readEpub(FileInputStream(path))

        return bookMetadata!!.metadata.firstTitle
    }

    // returns compressed cover image
    fun getCoverImage(): ByteArray? {
        if(bookMetadata != null) {
            return ImageConverter.compressBitmapToByteArray(bookMetadata?.coverImage!!.data)
        }
        return null
    }

    fun getCurrentPage(): String {
        return retrievePageFromReader(currentPageIndex)
    }

    // Reader functions
    /**
     * Decrements the page index and retrieves the content for the previous page
     */
    fun turnPageLeft(): String {
        if (currentPageIndex > 1) {
            currentPageIndex--
        }

        db!!.updateTimestampAndPageNumber(bookInfo!!.bookName, currentPageIndex)
        return retrievePageFromReader(currentPageIndex)
    }

    /**
     * Increments the page index and retrieves the content for the next page.
     */
    fun turnPageRight(): String {

        var content = ""

        content = try {
            currentPageIndex++
            retrievePageFromReader(currentPageIndex)
        } catch(e: OutOfPagesException) {
            currentPageIndex--
            retrievePageFromReader(currentPageIndex)
        }

        db!!.updateTimestampAndPageNumber(bookInfo!!.bookName, currentPageIndex)

        return content

    }

    @Throws(OutOfPagesException::class)
    private fun retrievePageFromReader(index: Int): String {
        var content = ""
        try {
            val bookSection = bookContent!!.readSection(index)
            content = bookSection.sectionTextContent

            // TODO: check for chapters.

        } catch (readingError: ReadingException) {
            Log.w("Reading Error", readingError)
        }

        return content
    }

    fun configureReader(path: String, maxStringSize: Int): Boolean {

        bookContent!!.setMaxContentPerSection(maxStringSize)
        bookContent!!.setIsIncludingTextContent(true)

        val success = try {
            bookContent!!.setFullContent(path)
            true
        } catch (e: ReadingException) {
            false
        }

        return success
    }

    /**
     *
     * @return -1 if file is invalid, 0 if file already exists, 1 if file is successfully added.
     */
    fun addEpubFileToDB(bookPath: String, fontSize: Int, maxStringSize: Int): Int {
        // retrieving metadata from book
        val bookName = setBookFromFile(bookPath)
        val bookThumbnail = getCoverImage()!!

        // add books to library and set thumbnail
        if(bookExistsDB(bookName)) {
            bookInfo = db!!.getBook(bookName)
            return 0
        }

        // adds book to library and configures reader
        db!!.addBook(bookName, bookPath, bookThumbnail, fontSize, System.currentTimeMillis())
        bookInfo = db!!.getBook(bookName)

        // Reader is configured.
        if(configureReader(bookPath, maxStringSize)) {
            return 1
        } else {
            return -1
        }
    }

    // DB functions
    fun clearDB() {
        db!!.clearDB()
    }

    fun updateFontSize(updatedFontSize: Int, updatedMaxStringSize: Int) {
        // TODO: Font To Page Conversion
        // get Font Size and Page Number, based on stored BookInfo.
        val maxStringLength = SettingsManager.getMaxStringSizeByFontSize(bookInfo!!.fontSize)
        val pageNumber = bookInfo!!.pageNumber

        // calculate pageNumber from this
        val charTotal = pageNumber * maxStringLength!! // the character the top of the page is at.
        val newPageNumber = charTotal / updatedMaxStringSize

        // update database
        db!!.updateTimestampAndPageNumber(bookInfo!!.bookName, newPageNumber)
        db!!.updateFontSize(bookInfo!!.bookName, updatedFontSize)

        // reconfigure reader
        configureReader(bookInfo!!.bookPath, updatedMaxStringSize)
        currentPageIndex = newPageNumber
        retrievePageFromReader(currentPageIndex)

    }

    fun getBooksFromDB(): ArrayList<StoredBook> {
        return db!!.getListOfBooks()
    }

    private fun bookExistsDB(bookName: String): Boolean {
        return (db!!.getBook(bookName) != null)
    }

}