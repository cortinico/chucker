package com.chuckerteam.chucker.internal.data.roomentity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chuckerteam.chucker.api.datamodel.HttpBody
import com.chuckerteam.chucker.api.datamodel.HttpHeader

@Entity(tableName = "responses")
internal data class RoomHttpResponse(
        @PrimaryKey val id: Long,
        @Embedded var body: RoomHttpBody? = null,
        @ColumnInfo(name = "date") val date: Long,
        @ColumnInfo(name = "headersSize") var headersSize: Long,
        @ColumnInfo(name = "protocol") val protocol: String,
        @ColumnInfo(name = "code") val code: Int,
        @Embedded var redactedHeaders: List<RoomHttpHeader>,
        @ColumnInfo(name = "tlsVersion") val tlsVersion: String? = null,
        @ColumnInfo(name = "cipherSuite") val cipherSuite: String? = null,
        @ColumnInfo(name = "message") val message: String,
        @ColumnInfo(name = "contentType") val contentType: String? = null,
        @ColumnInfo(name = "tookMs") val tookMs: Long,
)
