package com.chukwuka.schedule.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var description: String,
    var timeInMillis: Long,
    var dayOfMonth: Int,
    var month: Int,
    var year: Int
) : Parcelable {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeInt(id)
            it.writeString(description)
            it.writeLong(timeInMillis)
            it.writeInt(dayOfMonth)
            it.writeInt(month)
            it.writeInt(year)
        }
    }

    @Ignore
    constructor(internal: Parcel) : this(
        id = internal.readInt(),
        description = internal.readString().orEmpty(),
        timeInMillis = internal.readLong(),
        dayOfMonth = internal.readInt(),
        month = internal.readInt(),
        year = internal.readInt()
    )

    companion object CREATOR : Parcelable.Creator<Schedule> {
        override fun createFromParcel(parcel: Parcel): Schedule {
            return Schedule(parcel)
        }

        override fun newArray(size: Int): Array<Schedule?> {
            return arrayOfNulls(size)
        }
    }
}