package com.chuckerteam.chucker.internal.data.roomentity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chuckerteam.chucker.api.datamodel.HttpTransactionStatus
import com.chuckerteam.chucker.api.datamodel.HttpRequest
import com.chuckerteam.chucker.api.datamodel.HttpResponse

@Entity(tableName = "transactions")
internal data class RoomHttpTransaction(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "status") var status: HttpTransactionStatus = HttpTransactionStatus.Created,
        @ColumnInfo(name = "errorMessage") var errorMessage: String? = null,
        @Embedded var request: HttpRequest? = null,
        @Embedded var response: HttpResponse? = null,
)
