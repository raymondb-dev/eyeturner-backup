package raymondbdev.eyeturner.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class ImageConverter {

    companion object {
        fun byteToBitmap(b: ByteArray?): Bitmap? {
            return if (b == null || b.isEmpty()) null else BitmapFactory
                .decodeByteArray(b, 0, b.size)
        }

        fun compressBitmapToByteArray(byteArray: ByteArray): ByteArray {
            val originalImage = byteToBitmap(byteArray)
            val outputStream = ByteArrayOutputStream()
            originalImage!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }
    }
}