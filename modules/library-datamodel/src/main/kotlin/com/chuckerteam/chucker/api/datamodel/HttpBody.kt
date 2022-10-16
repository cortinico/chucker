package com.chuckerteam.chucker.api.datamodel

sealed class HttpBody(open val payloadSize: Long) {
    class Decoded(val value: String, override val payloadSize: Long) : HttpBody(payloadSize)
    class EncodedImageData(val value: ByteArray, override val payloadSize: Long) : HttpBody(payloadSize)
    class Encoded(override val payloadSize: Long) : HttpBody(payloadSize)
    object Empty : HttpBody(0)
}
