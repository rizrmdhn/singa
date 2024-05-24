package com.singa.asl.utils

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
import com.singa.asl.BuildConfig
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

    private fun createVideoFile(): File {
        val videoFileName = "MP4_$timeStamp.mp4"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(videoFileName, ".mp4", storageDir)
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun File.reduceFileImage(): File {
        val file  = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun createImageFromBitmap(context: Context, bitmap: Bitmap): String? {
        // Define the directory to save the image
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Generate a unique filename
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        // Create the file
        val imageFile: File? = try {
            File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        // If the file was created successfully, save the bitmap to it
        if (imageFile != null) {
            try {
                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.flush()
                }
                return imageFile.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun createImageFile(): File {
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun getUri(context: Context): Uri? {
        return  FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            createVideoFile()
        )
    }

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
            "${BuildConfig.APPLICATION_ID}.fileprovider",
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