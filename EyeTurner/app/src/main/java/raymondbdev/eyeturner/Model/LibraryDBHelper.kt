package raymondbdev.eyeturner.Model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Helper class for DB operations. Allows persistive data to be used.
 */
class LibraryDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {

        val createLibraryTableSql = "CREATE TABLE IF NOT EXISTS Library(" +
                "id INT NOT NULL," +
                "bookName TEXT," +
                "bookPath TEXT," +
                "pageNumber INT," +
                "image TEXT," +
                "UNIQUE(id)" +
                ");";

        db.execSQL(createLibraryTableSql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun addBook(bookName: String, bookPath: String, imageByteArray: ByteArray) {
        val db = this.writableDatabase
        val addBookSql = "UPDATE Library " +
                String.format("SET bookName=%s ", bookName) +
                String.format("SET bookPath=%s ", bookPath) +
                String.format("SET image=%s ", imageByteArray) +
                String.format("SET pageNumber=%d ", 1) +
                String.format("SET timestampLastUsed=% ", 1)
        db.execSQL(addBookSql)
    }

    fun getListOfBooks(): List<StoredBook> {

        var bookList = ArrayList<StoredBook>()

        val db = this.readableDatabase
        val getBooksSql = "SELECT bookName, bookPath, pageNumber, image FROM Library"
        val bookCursor = db.rawQuery(getBooksSql, null);

        while(bookCursor.moveToNext()) {
            val bookName = bookCursor.getString(0)
            val bookPath = bookCursor.getString(1)
            val pageNumber = bookCursor.getInt(2)
            val image = ImageConverter.byteToBitmap(bookCursor.getBlob(3))
            bookList.add(StoredBook(bookName, bookPath, pageNumber, image!!))
        }

        bookCursor.close()

        return bookList
    }

    fun getCurrentPageNumber(bookName: String): Int {
        val db = this.readableDatabase

        val getPageNumberSql = String.format("SELECT pageNumber FROM Library WHERE bookName=%s", bookName)
        val bookCursor = db.rawQuery(getPageNumberSql, null);

        bookCursor.moveToFirst()
        val pageNumber = bookCursor.getInt(0)
        bookCursor.close()

        return pageNumber
    }

    fun storePageNumber(bookName: String, pageNumber: Int) {
        val db = this.writableDatabase
        val updatePageNumberSql = "UPDATE PiggyBank " +
                String.format("SET pageNumber=%d ", pageNumber) +
                String.format("WHERE bookName=%s ", bookName)

        db.execSQL(updatePageNumberSql)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }
}