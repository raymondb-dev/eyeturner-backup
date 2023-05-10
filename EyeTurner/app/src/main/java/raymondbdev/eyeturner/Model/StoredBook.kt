package raymondbdev.eyeturner.Model

import android.graphics.Bitmap
import java.util.Date

class StoredBook(val bookName: String,
                 val bookPath: String,
                 val pageNumber: Int,
                 val image: Bitmap?,
                 var fontSize: Int,
                 val timestamp: Date)