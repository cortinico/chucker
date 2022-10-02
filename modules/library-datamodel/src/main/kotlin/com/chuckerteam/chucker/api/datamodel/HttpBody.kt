package com.chuckerteam.chucker.api.datamodel

sealed interface HttpBody {
    data class Decoded(val value: String) : HttpBody
    data class Encoded(val value: ByteArray) : HttpBody
}
