package com.chuckerteam.chucker.internal.data.entity

import androidx.room.ColumnInfo
import java.text.DateFormat

/**
 * A subset of [RecordedThrowable] to perform faster Read operations on the Repository.
 * This Tuple is good to be used on List or Preview interfaces.
 */
internal data class RecordedThrowableTuple(
    @ColumnInfo(name = "id") var id: Long? = 0,
    @ColumnInfo(name = "tag") var tag: String?,
    @ColumnInfo(name = "date") var date: Long?,
    @ColumnInfo(name = "clazz") var clazz: String?,
    @ColumnInfo(name = "message") var message: String?
) {
    val formattedDate: String
        get() {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
                .format(this.date)
        }
}
