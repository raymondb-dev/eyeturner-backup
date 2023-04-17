package raymondbdev.eyeturner.Model

import android.util.Log
import com.github.mertakdut.Reader
import com.github.mertakdut.exception.OutOfPagesException
import com.github.mertakdut.exception.ReadingException
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import java.io.InputStream

class ReadingHelper(newReader: Reader) {
    var reader: Reader? = null
    var book: Book? = null
    var currentPageIndex = 1

    init {
        reader = newReader
    }

    fun getBookFromFile(fileName: String) {
        var epubReader = EpubReader()
        book = epubReader.readEpub(FileInputStream(fileName))
    }

    fun getCoverImage(): InputStream? {
        return book?.coverImage?.inputStream
    }

    fun getCurrentPage(): String {
        return retrievePageFromReader(currentPageIndex)
    }

    /**
     * Decrements the page index and retrieves the content for the previous page
     */
    fun turnPageLeft(): String {
        if (currentPageIndex > 1) {
            currentPageIndex--
        }

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

        return content

    }

    @Throws(OutOfPagesException::class)
    private fun retrievePageFromReader(index: Int): String {
        var content = ""
        try {
            val bookSection = reader!!.readSection(index)
            content = bookSection.sectionTextContent

            // TODO: check for chapters.

        } catch (readingError: ReadingException) {
            Log.w("Reading Error", readingError)
        }
        return content
    }

}