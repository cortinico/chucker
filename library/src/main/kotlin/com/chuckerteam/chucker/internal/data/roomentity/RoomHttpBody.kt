package com.chuckerteam.chucker.internal.data.roomentity

internal sealed interface RoomHttpBody {
    data class Decoded(val value: String) : RoomHttpBody
    data class Encoded(val value: ByteArray) : RoomHttpBody
}
