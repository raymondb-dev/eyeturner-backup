package raymondbdev.eyeturner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import raymondbdev.eyeturner.Model.StoredBook
import raymondbdev.eyeturner.R

class BookCardAdapter(private val context: Context, listOfBooks: ArrayList<StoredBook>) :
    RecyclerView.Adapter<BookCardAdapter.ViewHolder>() {

    private val listOfBooks: ArrayList<StoredBook>

    // Constructor
    init {
        this.listOfBooks = listOfBooks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // to inflate the layout for each item of recycler view.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.book_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // to set data to textview and imageview of each card layout
        val model = listOfBooks[position]
        holder.bookThumbnail.setImageBitmap(model.image)
    }

    override fun getItemCount(): Int {
        // this method is used for showing number of card items in recycler view.
        return listOfBooks.size
    }

    // View holder class for initializing of your views such as TextView and Imageview.
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // private val bookThumbnail: ImageView
        val bookThumbnail: ImageView

        init {
            bookThumbnail = itemView.findViewById(R.id.bookCard)
        }
    }
}