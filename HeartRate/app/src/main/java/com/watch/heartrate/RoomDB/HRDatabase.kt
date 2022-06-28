package com.watch.heartrate.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HeartRateModel::class], version = 2, exportSchema = false)
abstract class HRDatabase : RoomDatabase() {
    abstract val HRDatabaseDAO: HRDatabaseDAO

    companion object {
        @Volatile
        var INSTANCE: HRDatabase? = null

        fun getInstance(context: Context): HRDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    //database using Room Database Builder
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HRDatabase::class.java, "tasks_history_Database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance

                }
                return instance

            }

        }
    }
}

