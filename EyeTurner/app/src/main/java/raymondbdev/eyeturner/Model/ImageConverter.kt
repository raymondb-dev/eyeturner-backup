package raymondbdev.eyeturner.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageConverter {

    companion object {
        fun byteToBitmap(b: ByteArray?): Bitmap? {
            return if (b == null || b.isEmpty()) null else BitmapFactory
                .decodeByteArray(b, 0, b.size)
        }
    }
}