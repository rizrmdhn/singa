package com.singa.asl.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.singa.asl.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Helpers {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private const val MAXIMAL_SIZE = 1000000
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun convertToUserLocalTime(utcDateTime: String, pattern: String = "HH:mm"): String {
        // Parse the input UTC date-time string
        val utcFormatter = DateTimeFormatter.ISO_DATE_TIME
        val localDateTime = LocalDateTime.parse(utcDateTime, utcFormatter)

        // Convert to ZonedDateTime in UTC
        val utcZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))

        // Get the user's time zone
        val userTimeZone = TimeZone.getDefault()
        val userZoneId = userTimeZone.toZoneId()

        // Convert to the user's local time zone
        val userZonedDateTime = utcZonedDateTime.withZoneSameInstant(userZoneId)

        // Format the result in the desired pattern
        val targetFormatter = DateTimeFormatter.ofPattern(pattern)
        return userZonedDateTime.format(targetFormatter)
    }

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

    suspend fun applyVideoEffects(inputFile: File, outputFile: File, onProgress: (Int, Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            // Function to get the duration of the video in milliseconds
            fun getVideoDuration(file: File): Long {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(file.absolutePath)
                val durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durationStr?.toLong() ?: 0L
                mediaMetadataRetriever.release()
                return duration
            }

            // Get total duration of the input video
            val totalDuration = getVideoDuration(inputFile)
            println("Total duration: $totalDuration milliseconds")

            // Enable FFmpeg log callback to track progress
            Config.enableLogCallback { logMessage ->
                println(logMessage.text)
                val matchResult = Regex("time=(\\d+):(\\d+):(\\d+).(\\d+)").find(logMessage.text)
                matchResult?.let { result ->
                    val (hours, minutes, seconds, milliseconds) = result.destructured
                    val currentTime = hours.toInt() * 3600 * 1000 +
                            minutes.toInt() * 60 * 1000 +
                            seconds.toInt() * 1000 +
                            milliseconds.toInt()

                    val currentProgress = ((currentTime.toDouble() / totalDuration) * 100).toInt()
                    println("Current time: $currentTime milliseconds, Progress: $currentProgress%")
                    onProgress(currentProgress, true)
                }
            }

            // FFmpeg command for mirroring the video
            val command = arrayOf(
                "-i", inputFile.absolutePath,                // Input file
                "-vf", "hflip",                              // Mirror effect (horizontal flip)
                "-c:v", "libx264",                           // Compress using H.264 codec
                "-preset", "fast",                           // Compression preset
                "-crf", "28",                                // Compression quality
                outputFile.absolutePath                      // Output file
            )

            val rc = FFmpeg.execute(command)

            // Reset FFmpeg statistics and log callbacks
            Config.resetStatistics()

            if (rc != 0) {
                // Handle error if FFmpeg command fails
                throw IOException("FFmpeg execution failed with return code $rc")
            } else {
                // Ensure progress reaches 100% on successful completion
                onProgress(100, false)
            }
        }
    }

}