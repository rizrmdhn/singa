package com.singa.core.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer
import java.io.IOException


open class ReqBodyWithProgress(
    private val multipartBody: MultipartBody.Part,
    var onUploadProgress: ((progress: Int) -> Unit)? = null
) : RequestBody() {
    private var mCountingSink: CountingSink? = null

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return multipartBody.body.contentLength()
    }

    override fun contentType(): MediaType? {
        return multipartBody.body.contentType()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        mCountingSink = CountingSink(sink)
        val bufferedSink: BufferedSink = mCountingSink!!.buffer()
        multipartBody.body.writeTo(bufferedSink)
        try {
            bufferedSink.flush()
        } catch (e: IOException) {
            // Handle write error here (e.g., log the error)
        }
    }

    protected inner class CountingSink(delegate: Sink?) : ForwardingSink(delegate!!) {
        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            bytesWritten += byteCount
            onUploadProgress?.invoke((100f * bytesWritten / contentLength()).toInt())
            super.write(source, byteCount)
            delegate.flush() // I have added this line to manually flush the sink
        }
    }
}