package com.chuckerteam.chucker.internal.support.processors

import com.chuckerteam.chucker.api.BodyDecoder
import com.chuckerteam.chucker.api.datamodel.HttpBody
import com.chuckerteam.chucker.api.datamodel.HttpResponse
import com.chuckerteam.chucker.internal.support.*
import com.chuckerteam.chucker.internal.support.CacheDirectoryProvider
import com.chuckerteam.chucker.internal.support.DepletingSource
import com.chuckerteam.chucker.internal.support.FileFactory
import com.chuckerteam.chucker.internal.support.Logger
import com.chuckerteam.chucker.internal.support.ReportingSink
import com.chuckerteam.chucker.internal.support.TeeSource
import okhttp3.Response as OkHttpResponse
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import okio.ByteString
import okio.IOException
import okio.Source
import okio.buffer
import okio.source
import java.io.File

internal class ResponseProcessor(
        override val maxContentLength: Long,
        override val headersToRedact: Set<String>,
        private val bodyDecoders: List<BodyDecoder>,
        private val cacheDirectoryProvider: CacheDirectoryProvider,
        private val alwaysReadResponseBody: Boolean,
        private val onResponseUpdatedCallback: (HttpResponse) -> Unit,
) : CallProcessor<OkHttpResponse, HttpResponse> {

    override fun processCall(input: OkHttpResponse): HttpResponse {
        // We can tell now if the body is empty or not. If the body is not empty, we set it to null
        // here as when we multicast the response, it will be populated.
        val provisionalBody = if (!input.hasBody() || input.body == null) {
            HttpBody.Empty
        } else {
            null
        }

        return HttpResponse(
                headersSize = input.headers.byteCount(),
                redactedHeaders = processHeaders(input.headers),
                date = input.receivedResponseAtMillis,
                code = input.code,
                message = input.message,
                tlsVersion = input.handshake?.tlsVersion?.javaName,
                cipherSuite = input.handshake?.cipherSuite?.javaName,
                protocol = input.protocol.toString(),
                contentType = input.contentType,
                tookMs = (input.receivedResponseAtMillis - input.sentRequestAtMillis),
                body = provisionalBody
        )
    }

    internal fun multiCastResponse(input: OkHttpResponse, entity: HttpResponse): OkHttpResponse {
        val responseBody = input.body
        if (!input.hasBody() || responseBody == null) {
            return input
        }

        val contentType = responseBody.contentType()
        val contentLength = responseBody.contentLength()

        val sideStream = ReportingSink(
                createTempTransactionFile(),
                ResponseReportingSinkCallback(input, entity, onResponseUpdatedCallback),
                maxContentLength
        )
        var upstream: Source = TeeSource(responseBody.source(), sideStream)
        if (alwaysReadResponseBody) upstream = DepletingSource(upstream)

        return input.newBuilder()
                .body(upstream.buffer().asResponseBody(contentType, contentLength))
                .build()
    }

    private fun createTempTransactionFile(): File? {
        val cache = cacheDirectoryProvider.provide()
        return if (cache == null) {
            Logger.warn("Failed to obtain a valid cache directory for transaction files")
            null
        } else {
            FileFactory.create(cache)
        }
    }

    private fun processPayload(input: OkHttpResponse, payload: Buffer, sourceByteCount: Long): HttpBody {
        val responseBody = input.body ?: return HttpBody.Empty
        val contentType = responseBody.contentType()
        val isImageContentType = contentType?.toString()?.contains(CONTENT_TYPE_IMAGE, ignoreCase = true) == true

        return if (isImageContentType && payload.size < MAX_BLOB_SIZE) {
            HttpBody.EncodedImageData(payload.readByteArray(), sourceByteCount)
        } else if (payload.size != 0L) {
            val decodedContent = decodePayload(input, payload.readByteString())
            if (decodedContent != null) {
                HttpBody.Decoded(decodedContent, sourceByteCount)
            } else {
                HttpBody.Encoded(sourceByteCount)
            }
        } else {
            HttpBody.Empty
        }
    }

    private fun decodePayload(input: OkHttpResponse, body: ByteString) = bodyDecoders.asSequence()
            .mapNotNull { decoder ->
                try {
                    decoder.decodeResponse(input, body)
                } catch (e: IOException) {
                    Logger.warn("Decoder $decoder failed to process response payload", e)
                    null
                }
            }.firstOrNull()

    private inner class ResponseReportingSinkCallback(
            private val input: OkHttpResponse,
            private val entity: HttpResponse,
            private val onResponseDecodedCallback: (HttpResponse) -> Unit
    ) : ReportingSink.Callback {

        override fun onClosed(file: File?, sourceByteCount: Long) {
            file?.readResponsePayload()?.let { payload ->
                entity.body = processPayload(input, payload, sourceByteCount)
                onResponseDecodedCallback(entity)
            }
            file?.delete()
        }

        override fun onFailure(file: File?, exception: java.io.IOException) {
            Logger.error("Failed to read response payload", exception)
        }

        private fun File.readResponsePayload() = try {
            source().uncompress(input.headers).use { source ->
                Buffer().apply { writeAll(source) }
            }
        } catch (e: java.io.IOException) {
            Logger.error("Response payload couldn't be processed", e)
            null
        }
    }

    private companion object {
        const val MAX_BLOB_SIZE = 1_000_000L

        const val CONTENT_TYPE_IMAGE = "image"
    }
}
