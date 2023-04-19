package raymondbdev.eyeturner.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import raymondbdev.eyeturner.Model.StoredBook

class BookLibraryAdapter(private val context: Context, private var books: ArrayList<StoredBook>): BaseAdapter() {

    // assumed that adapter is initially empty
    var selectedItemIndex = -1

    // returns the number of images, in our example it is 10
    override fun getCount(): Int {
        return books.size
    }

    /**
     * Returns the Item of an item, i.e. for our example we can get the image
     */
    override fun getItem(position: Int): Any {
        return position
    }

    // returns the ID of an item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addItem(book: StoredBook) {
        // adds new book to the end of the list
        books.add(book)
        selectedItemIndex++
    }

    fun getPreviousItem(): StoredBook? {
        if(selectedItemIndex > 0) {
            selectedItemIndex--
        }

        if(selectedItemIndex > -1) {
            return books[selectedItemIndex]
        }

        return null

    }

    fun getNextItem(): StoredBook? {
        if(selectedItemIndex < (books.size - 1) - 1) {
            selectedItemIndex++
        }

        if(selectedItemIndex > -1) {
            return books[selectedItemIndex]
        }

        return null
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {

        // sets image from each item in the view.
        val imageView = ImageView(context)
        val bitmap = books[position].image
        imageView.setImageBitmap(bitmap)

        return imageView
    }
}
