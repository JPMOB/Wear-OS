package com.watch.heartrate.ViewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.watch.heartrate.RoomDB.HRDatabaseDAO

class TaskViewModelFactory(private  val dataSource: HRDatabaseDAO, private  val application: Application) :
 ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HRViewModel::class.java)) {
            return HRViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}