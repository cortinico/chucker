package com.chuckerteam.chucker.internal.data.roomentity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chuckerteam.chucker.api.datamodel.HttpBody
import com.chuckerteam.chucker.api.datamodel.HttpHeader

@Entity(tableName = "requests")
internal data class RoomHttpRequest(
        @PrimaryKey val id: Long,
        @ColumnInfo(name = "url") val url: String,
        @ColumnInfo(name = "host") val host: String,
        @ColumnInfo(name = "path") val path: String,
        @ColumnInfo(name = "scheme") val scheme: String,
        @ColumnInfo(name = "headersSize") var headersSize: Long,
        @Embedded var redactedHeaders: List<RoomHttpHeader>,
        @ColumnInfo(name = "method") val method: String,
        @Embedded val body: RoomHttpBody,
        @ColumnInfo(name = "date") var date: Long,
        @ColumnInfo(name = "payloadSize") val payloadSize: Long,
        @ColumnInfo(name = "contentType") val contentType: String? = null,
        @ColumnInfo(name = "graphQlDetected") val graphQlDetected: Boolean = false,
        @ColumnInfo(name = "graphQlOperationName") val graphQlOperationName: String? = null,
)
