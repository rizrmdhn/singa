package com.singa.asl.common

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.singa.core.BuildConfig
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Helpers {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private const val MAXIMAL_SIZE = 1000000
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

//    fun convertStringToClassifciations(result: String): List<Classifications> {
//        return parseJson(result)
//    }

    fun uriToBlob(context: Context, uri: Uri): ByteArray? {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        if (inputStream != null) {
            return try {
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                inputStream.close()
            }
        }

        return null
    }

    fun getImageBitmap(imageBlob: ByteArray): Bitmap? {
        return try {
            val inputStream = ByteArrayInputStream(imageBlob)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun parseISODateString(isoDateString: String): LocalDateTime? {
        if (isoDateString.isBlank()) return null // Return null if the input string is empty or blank

        return try {
            LocalDateTime.parse(isoDateString, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatDate(dateTime: LocalDateTime?): String {
        if (dateTime == null) {
            return "Invalid date/time" // or any other appropriate default value or error message
        }

        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss", Locale("id", "ID"))
        return formatter.format(dateTime)
    }

    fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }

            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }

        return uri ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.LIBRARY_PACKAGE_NAME}.fileprovider",
            imageFile
        )
    }

//    private fun parseJson(jsonString: String): List<Classifications> {
//        val pattern = """<Category "([^"]+)" \(displayName= score=([^ ]+) index=(\d)\)>""".toRegex()
//
//        val categoriesList = mutableListOf<Category>()
//        val headIndexList = mutableListOf<Int>()
//
//        pattern.findAll(jsonString).forEach { matchResult ->
//            val (name, score, index) = matchResult.destructured
//            categoriesList.add(Category(name, "", score.toDouble(), index.toInt()))
//        }
//
//        val classificationsList = mutableListOf<Classifications>()
//
//        // Assuming only one Classifications object in the JSON string
//        classificationsList.add(Classifications(categoriesList, 0))
//
//        return classificationsList
//    }
}