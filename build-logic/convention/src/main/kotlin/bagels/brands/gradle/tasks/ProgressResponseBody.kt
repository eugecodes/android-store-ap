package bagels.brands.gradle.tasks

import okhttp3.Interceptor
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import org.gradle.internal.logging.progress.ProgressLogger

private const val ONE_MEGABYTE_IN_BYTES: Double = (1L * 1024L * 1024L).toDouble()

private val Long.mb: String
    get() = String.format("%.2f", this / ONE_MEGABYTE_IN_BYTES)

internal class ProgressResponseBody
internal constructor(
    private val responseBody: ResponseBody,
    private val progressListener: ProgressListener
) : ResponseBody() {

    private val bufferedSource: BufferedSource by lazy { source(responseBody.source()).buffer() }

    override fun contentType() = responseBody.contentType()

    override fun contentLength() = responseBody.contentLength()

    override fun source() = bufferedSource

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead: Long = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                return bytesRead
            }
        }
    }
}

internal interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}

internal class ProgressReportingInterceptor(private val progressListener: ProgressListener) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse
            .newBuilder()
            .body(ProgressResponseBody(originalResponse.body!!, progressListener))
            .build()
    }
}

/** A [ProgressListener] that logs progress to a [ProgressLogger]. */
internal class ProgressLoggerProgressListener(
    private val name: String,
    private val progressLogger: ProgressLogger
) : ProgressListener {
    private var firstUpdate = true

    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        if (done) {
            progressLogger.progress("Download completed")
        } else {
            if (firstUpdate) {
                firstUpdate = false
                if (contentLength == -1L) {
                    progressLogger.completed("content-length: unknown", /* failed */ true)
                    error("content-length: unknown")
                }
            }
            if (contentLength != -1L) {
                progressLogger.progress("$name > ${bytesRead.mb} MB/${contentLength.mb} MB")
            }
        }
    }
}
