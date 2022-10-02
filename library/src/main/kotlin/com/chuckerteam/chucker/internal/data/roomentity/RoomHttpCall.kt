package com.chuckerteam.chucker.internal.data.roomentity

internal data class RoomHttpCall(
    val id: Long,
    val url: String,
    val host: String,
    val path: String,
    val scheme: String,
    val error: String,
    val method: String,
    val request: RoomHttpRequest,
    val response: RoomHttpResponse,
    val tookMs: Long,
    val protocol: String
)
