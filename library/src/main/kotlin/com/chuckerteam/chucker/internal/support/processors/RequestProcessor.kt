package com.chuckerteam.chucker.internal.support.processors

import com.chuckerteam.chucker.api.BodyDecoder
import com.chuckerteam.chucker.api.datamodel.HttpBody
import com.chuckerteam.chucker.api.datamodel.HttpRequest
import com.chuckerteam.chucker.internal.support.*
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse
import okio.Buffer
import okio.ByteString
import okio.IOException

internal class RequestProcessor(
        override val maxContentLength: Long,
        override val headersToRedact: Set<String>,
        private val bodyDecoders: List<BodyDecoder>,
) : CallProcessor<OkHttpRequest, HttpRequest> {
    override fun processCall(input: OkHttpRequest): HttpRequest {
        val formattedUrl = FormattedUrl.fromHttpUrl(input.url, encoded = false)
        val headers = processHeaders(input.headers)
        val grahpQlOperationName =
                headers.find { it.name.contains("operation-name", ignoreCase = true) }?.value
        val graphQlDetected = isGraphQLRequest(grahpQlOperationName, input)
        val entityBody = processBody(input)

        return HttpRequest(
                url = formattedUrl.url,
                host = formattedUrl.host,
                path = formattedUrl.pathWithQuery,
                scheme = formattedUrl.scheme,
                headersSize = input.headers.byteCount(),
                redactedHeaders = headers,
                method = input.method,
                body = entityBody,
                date = System.currentTimeMillis(),
                payloadSize = input.body?.contentLength() ?: 0L,
                contentType = input.body?.contentType()?.toString(),
                graphQlDetected = graphQlDetected,
                graphQlOperationName = grahpQlOperationName,
        )
    }

    fun processAfterResponse(input: OkHttpResponse, entity: HttpRequest): HttpRequest =
            entity.apply {
                headersSize = input.request.headers.byteCount()
                redactedHeaders = processHeaders(input.request.headers)
                date = input.sentRequestAtMillis
            }

    private fun processBody(input: OkHttpRequest): HttpBody {
        val body = input.body ?: return HttpBody.Empty
        val payloadSize = input.body?.contentLength() ?: 0L
        if (body.isOneShot()) {
            Logger.info("Skipping one shot request body")
            return HttpBody.Empty
        }
        if (body.isDuplex()) {
            Logger.info("Skipping duplex request body")
            return HttpBody.Empty
        }

        val requestSource: Buffer = try {
            Buffer().apply { body.writeTo(this) }
        } catch (e: IOException) {
            Logger.error("Failed to read request payload", e)
            return HttpBody.Empty
        }
        val limitingSource =
                LimitingSource(requestSource.uncompress(input.headers), maxContentLength)
        val contentBuffer = Buffer().apply { limitingSource.use { writeAll(it) } }
        var decodedContent = decodePayload(input, contentBuffer.readByteString())
        if (decodedContent != null && limitingSource.isThresholdReached) {
            decodedContent += "\n\n--- Content truncated ---"
        }
        return if (decodedContent != null) {
            HttpBody.Decoded(decodedContent, payloadSize)
        } else {
            HttpBody.Encoded(payloadSize)
        }
    }

    private fun decodePayload(request: OkHttpRequest, body: ByteString) = bodyDecoders.asSequence()
            .mapNotNull { decoder ->
                try {
                    Logger.info("Decoding with: $decoder")
                    decoder.decodeRequest(request, body)
                } catch (e: IOException) {
                    Logger.warn("Decoder $decoder failed to process request payload", e)
                    null
                }
            }.firstOrNull()

    private fun isGraphQLRequest(graphQLOperationName: String?, request: OkHttpRequest) =
            graphQLOperationName != null ||
                    request.url.pathSegments.contains("graphql") ||
                    request.url.host.contains("graphql")
}
