package com.watch.heartrate.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_table")
data class HeartRateModel
    (
    @PrimaryKey(autoGenerate = true)
    var taskId:Long=0L,
    @ColumnInfo
    var avgHR: String,
    @ColumnInfo
    var time: String)