package com.chuckerteam.chucker.internal.data.roomentity

internal sealed class RoomHttpBody(open val payloadSize: Long) {
    class Decoded(val value: String, override val payloadSize: Long) : RoomHttpBody(payloadSize)
    class EncodedImageData(val value: ByteArray, override val payloadSize: Long) : RoomHttpBody(payloadSize)
    class Encoded(override val payloadSize: Long) : RoomHttpBody(payloadSize)
    object Empty : RoomHttpBody(0)
}
