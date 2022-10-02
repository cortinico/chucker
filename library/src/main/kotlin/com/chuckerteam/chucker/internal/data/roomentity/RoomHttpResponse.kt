package com.chuckerteam.chucker.internal.data.roomentity

internal data class RoomHttpResponse(
    val id: Long,
    val method: String,
    val redactedHeaders: List<RoomHttpHeader>,
    val unredactedHeadersByteSize: Long,
    val body: RoomHttpBody,
    val date: Long,
    val tlsVersion: String,
    val cipherSuite: String,
    val code: Int,
    val message: String,
    val payloadSize: Long,
    val contentType: String,
    val headers: String,
    val headersSize: Long,
    val isResponseBodyEncoded: Boolean = false,
    val responseImageData: ByteArray
)
