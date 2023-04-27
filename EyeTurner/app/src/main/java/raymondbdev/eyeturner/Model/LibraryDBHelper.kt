package raymondbdev.eyeturner.Model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


/**
 * Helper class for DB operations. Allows persistive data to be used.
 */
class LibraryDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {

        val createLibraryTableSql = "CREATE TABLE IF NOT EXISTS Library(" +
                "id INT PRIMARY KEY," +
                "bookName TEXT," +
                "bookPath TEXT," +
                "pageNumber INT," +
                "image BLOB," +
                "timestampLastUsed TIMESTAMP"+
                ");";

        db.execSQL(createLibraryTableSql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun addBook(bookName: String, bookPath: String, imageByteArray: ByteArray, timestamp: Long): Long {
        val db = this.writableDatabase

        val insertValues = ContentValues()
        insertValues.put("bookName", bookName) //These Fields should be your String values of actual column names
        insertValues.put("bookPath", bookPath)
        insertValues.put("image", imageByteArray)
        insertValues.put("pageNumber", 1)
        insertValues.put("timestampLastUsed",timestamp)

        val resultCode = db.insertOrThrow("Library", null, insertValues)
        db.close()

        return resultCode
    }

    fun removeBook(bookName: String) {
        val db = this.writableDatabase
        db.delete("Library", "bookName=?", arrayOf(bookName))
        db.close()
    }

    fun updateTimestampAndPageNumber(bookName: String, pageNumber: Int) {
        val db = this.writableDatabase

        val updateValues = ContentValues()
        updateValues.put("pageNumber", pageNumber)
        updateValues.put("timestampLastUsed", System.currentTimeMillis())

        db.update("Library", updateValues,"bookName=?", arrayOf(bookName))
        db.close()
    }

    fun getBook(bookName: String): StoredBook? {

        val db = this.readableDatabase
        val getBookSql = "SELECT * FROM Library WHERE bookName=? "
        val bookCursor = db.rawQuery(getBookSql, arrayOf(bookName));

        if (bookCursor.moveToFirst()) {
                val name = bookCursor.getString(1)
                val path = bookCursor.getString(2)
                val pageNumber = bookCursor.getInt(3)
                val image = ImageConverter.byteToBitmap(bookCursor.getBlob(4))
                val date = Date(bookCursor.getLong(5)) // converts milliseconds to Date

                bookCursor.close()
                db.close()

                return StoredBook(
                    name,
                    path,
                    pageNumber,
                    image,
                    date
                )
        }

        bookCursor.close()
        db.close()

        return null

    }

    fun getBookByPath(bookPath: String): StoredBook? {

        val db = this.readableDatabase
        val getBookSql = "SELECT * FROM Library WHERE bookPath=? "
        val bookCursor = db.rawQuery(getBookSql, arrayOf(bookPath));

        if (bookCursor.moveToFirst()) {
            val name = bookCursor.getString(1)
            val path = bookCursor.getString(2)
            val pageNumber = bookCursor.getInt(3)
            val image = ImageConverter.byteToBitmap(bookCursor.getBlob(4))
            val date = Date(bookCursor.getLong(5)) // converts milliseconds to Date

            bookCursor.close()
            db.close()

            return StoredBook(
                name,
                path,
                pageNumber,
                image,
                date
            )
        }

        bookCursor.close()
        db.close()

        return null

    }

    /**
     * Returns a list of stored books in order of timestamp.
     */
    fun getListOfBooks(): ArrayList<StoredBook> {

        val bookList = ArrayList<StoredBook>()

        val db = this.readableDatabase
        val getBooksSql = "SELECT * FROM Library ORDER BY timestampLastUsed DESC "
        val bookCursor = db.rawQuery(getBooksSql, null);

        if (bookCursor.moveToFirst()) {
            do {
                val bookName = bookCursor.getString(1)
                val bookPath = bookCursor.getString(2)
                val pageNumber = bookCursor.getInt(3)
                val image = ImageConverter.byteToBitmap(bookCursor.getBlob(4))
                val date = Date(bookCursor.getLong(5)) // converts milliseconds to Date
                bookList.add(StoredBook(bookName, bookPath, pageNumber, image, date))

            } while (bookCursor.moveToNext())
        }

        bookCursor.close()
        db.close()

        return bookList
    }

    fun getCurrentPageNumber(bookName: String): Int {
        val db = this.readableDatabase

        val getPageNumberSql = "SELECT pageNumber FROM Library WHERE bookName=?"
        val bookCursor = db.rawQuery(getPageNumberSql, arrayOf(bookName));

        bookCursor.moveToFirst()

        val pageNumber = bookCursor.getInt(0)

        bookCursor.close()
        db.close()

        return pageNumber
    }

    fun clearDB() {
        val db = this.writableDatabase
        val deleteTableSql = "DROP TABLE IF EXISTS Library"
        db.execSQL(deleteTableSql)
        onCreate(db)
        db.close()
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "bookLibrary.db"
    }
}