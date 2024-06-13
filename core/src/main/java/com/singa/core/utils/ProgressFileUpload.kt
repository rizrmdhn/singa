package com.singa.core.utils

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressFileUpload(
    private val file: File,
    private val contentType: MediaType?,
    private val progressListener: (Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? {
        return contentType
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inputStream = FileInputStream(file)
        var uploaded: Long = 0

        inputStream.use { input ->
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                uploaded += read
                sink.write(buffer, 0, read)
                val progress = (100 * uploaded / fileLength).toInt()
                progressListener(progress)
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}

