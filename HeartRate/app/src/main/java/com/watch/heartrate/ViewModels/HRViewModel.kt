package com.watch.heartrate.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.watch.heartrate.RoomDB.HRDatabaseDAO
import com.watch.heartrate.RoomDB.HeartRateModel
import kotlinx.coroutines.launch


class HRViewModel(var databaseDAO: HRDatabaseDAO, application: Application) :
    AndroidViewModel(application) {
    var tasks: LiveData<List<HeartRateModel>>? = null

    init {
        //we want to load all the task data as soon as the ViewModel get initialised so that we can show to user
        viewModelScope.launch {
            tasks = databaseDAO.getALlTask()
        }
    }

    fun addTask(heartRateModel: HeartRateModel) {
        viewModelScope.launch {
            databaseDAO.insert(heartRateModel)
        }
    }

    fun deleteAllTask() {
        viewModelScope.launch {
            databaseDAO.clearAllData()
        }
    }
}