package com.rago.keycloakclient.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "SHIPPER")
data class Shipper(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String
) : Parcelable
