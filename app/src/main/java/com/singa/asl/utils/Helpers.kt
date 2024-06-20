package com.singa.asl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                                              // Mirror effect (horizontal flip)
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

    fun bytesToMB(bytes: Long): Double {
        return bytes / (1024.0 * 1024.0)
    }
}