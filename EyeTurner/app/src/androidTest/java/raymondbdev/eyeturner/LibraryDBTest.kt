package raymondbdev.eyeturner

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import raymondbdev.eyeturner.Model.LibraryDBHelper
import java.util.*

/**
 * Test for Database functions
 */
class LibraryDBTest  {
    var dbHelper: LibraryDBHelper? = null

    @Before
    fun setupDb() {
        dbHelper = LibraryDBHelper(ApplicationProvider.getApplicationContext())
        dbHelper!!.clearDB()
    }

    @Test
    fun addBooksTest() {
        val defaultByteArray = ByteArray(100)

        for(i in 0..2) {
            dbHelper!!.addBook("Sample Book $i", "/path/to/book$i", defaultByteArray, i.toLong())
        }

        val books = dbHelper!!.getListOfBooks()
        Assert.assertEquals(3, books.size)

        // checks books are found in reverse order
        for(i in 0..2) {
            val book = books[i]
            val index = 2 - i
            Assert.assertEquals("Sample Book $index", book.bookName)
            Assert.assertEquals("/path/to/book$index", book.bookPath)
            Assert.assertEquals(Date(index.toLong()), book.timestamp)
        }

    }

    @Test
    fun emptyLibraryTest() {
        val books = dbHelper!!.getListOfBooks()
        Assert.assertEquals(0, books.size)
    }

    @Test
    fun removeBookTest() {
        val defaultByteArray = ByteArray(100)

        for(i in 0..2) {
            dbHelper!!.addBook("Sample Book $i", "/path/to/book$i", defaultByteArray, i.toLong())
        }

        dbHelper!!.removeBook("Sample Book 0")

        var books = dbHelper!!.getListOfBooks()
        Assert.assertEquals(2, books.size)
        Assert.assertNull(dbHelper!!.getBook("Sample Book 0"))
        Assert.assertNotNull(dbHelper!!.getBook("Sample Book 1"))
        Assert.assertNotNull(dbHelper!!.getBook("Sample Book 2"))

        dbHelper!!.removeBook("Sample Book 1")

        books = dbHelper!!.getListOfBooks()
        Assert.assertEquals(1, books.size)
        Assert.assertNull(dbHelper!!.getBook("Sample Book 1"))
        Assert.assertNotNull(dbHelper!!.getBook("Sample Book 2"))

        dbHelper!!.removeBook("Sample Book 2")

        books = dbHelper!!.getListOfBooks()
        Assert.assertEquals(0, books.size)
        Assert.assertNull(dbHelper!!.getBook("Sample Book 2"))
    }

    @Test
    fun pageNumberTest() {
        val defaultByteArray = ByteArray(100)
        val bookName = "Sample Book 1"

        dbHelper!!.addBook(
            bookName,
            "/path/to/book1",
            defaultByteArray,
            System.currentTimeMillis()
        )

        val initialDate = dbHelper!!.getBook(bookName)!!.timestamp

        dbHelper!!.updateTimestampAndPageNumber(bookName, 2)
        Assert.assertEquals(2, dbHelper!!.getCurrentPageNumber(bookName))

        dbHelper!!.updateTimestampAndPageNumber(bookName, 3)
        Assert.assertEquals(3, dbHelper!!.getCurrentPageNumber(bookName))

        dbHelper!!.updateTimestampAndPageNumber(bookName, 4)
        Assert.assertEquals(4, dbHelper!!.getCurrentPageNumber(bookName))

        dbHelper!!.updateTimestampAndPageNumber(bookName, 3)
        Assert.assertEquals(3, dbHelper!!.getCurrentPageNumber(bookName))

        val updatedDate = dbHelper!!.getBook(bookName)!!.timestamp

        // checks that date was update
        Assert.assertNotEquals(initialDate, updatedDate)
        Assert.assertTrue(updatedDate > initialDate)

    }

    @Test
    fun clearDBTest() {

    }
}