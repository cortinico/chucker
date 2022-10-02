package com.chuckerteam.chucker.internal.data.roomentity

internal data class RoomHttpRequest(
    val id: Long,
    val redactedHeaders: List<RoomHttpHeader>,
    val unredactedHeadersByteSize: Long,
    val body: RoomHttpBody,
    val date: Long,
    val payloadSize: Long,
    val contentType: String,
    val headers: String,
    val headersSize: Long,
    val isRequestBodyEncoded: Boolean = false,
    val graphQlDetected: Boolean = false,
    val graphQlOperationName: String
)
