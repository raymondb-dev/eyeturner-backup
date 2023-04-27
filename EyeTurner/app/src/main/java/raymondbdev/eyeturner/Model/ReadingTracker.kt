package raymondbdev.eyeturner.Model

import android.util.Log
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

    fun getCoverImage(): ByteArray? {
        if(bookMetadata != null) {
            return bookMetadata?.coverImage!!.data
        }
        return null
    }

    fun getBookName(): String? {
        return bookMetadata!!.metadata.firstTitle
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

    // DB functions
    fun clearDB() {
        db!!.clearDB()
    }

    fun getBooksFromDB(): ArrayList<StoredBook> {
        return db!!.getListOfBooks()
    }

    fun addBookToDB(bookName: String, bookPath: String, bookThumbnail: ByteArray, timestamp: Long) {
        db!!.addBook(bookName, bookPath, bookThumbnail, System.currentTimeMillis())
    }

    fun getBook(bookName: String): StoredBook? {
        return db!!.getBook(bookName)
    }

    fun bookExistsDB(bookName: String): Boolean {
        return (db!!.getBook(bookName) != null)
    }

}