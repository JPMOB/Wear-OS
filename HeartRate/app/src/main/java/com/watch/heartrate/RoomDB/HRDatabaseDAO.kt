package com.watch.heartrate.RoomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HRDatabaseDAO {
    @Insert
    suspend fun insert(heartRateModel: HeartRateModel)

    @Update
    suspend fun update(heartRateModel: HeartRateModel)

    @Query("DELETE from tasks_table")
    suspend fun clearAllData()
//we don't need to specify the getAllTask() as suspend function because Room by default uses background thread for function that return LiveData
    @Query("SELECT * FROM tasks_table ORDER BY taskId DESC ")
    fun getALlTask(): LiveData<List<HeartRateModel>>

}