package com.chuckerteam.chucker.internal.data.har.log.entry

import com.chuckerteam.chucker.api.datamodel.HttpTransaction
import com.chuckerteam.chucker.internal.data.har.log.entry.response.Content
import com.google.gson.annotations.SerializedName

// https://github.com/ahmadnassri/har-spec/blob/master/versions/1.2.md#response
// http://www.softwareishard.com/blog/har-12-spec/#response
internal data class Response(
    @SerializedName("status") val status: Int,
    @SerializedName("statusText") val statusText: String,
    @SerializedName("httpVersion") val httpVersion: String,
    @SerializedName("cookies") val cookies: List<Any> = emptyList(),
    @SerializedName("headers") val headers: List<Header>,
    @SerializedName("content") val content: Content? = null,
    @SerializedName("redirectURL") val redirectUrl: String = "",
    @SerializedName("headersSize") val headersSize: Long,
    @SerializedName("bodySize") val bodySize: Long,
    @SerializedName("totalSize") val totalSize: Long,
    @SerializedName("comment") val comment: String? = null
) {
    constructor(transaction: HttpTransaction) : this(
        status = transaction.response?.code ?: 0,
        statusText = transaction.response?.message ?: "",
        httpVersion = transaction.response?.protocol ?: "",
        headers = transaction.getParsedResponseHeaders()?.map { Header(it) } ?: emptyList(),
        content = transaction.response?.body?.payloadSize?.run { Content(transaction) },
        headersSize = transaction.response?.headersSize ?: 0,
        bodySize = transaction.getHarResponseBodySize(),
        totalSize = transaction.getResponseTotalSize()
    )
}
