package com.example.roomexample

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomexample.database.SceneDatabaseDao

//need the factory to pass an argument to the viewmodel
class MyViewModelFactory(private val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("unchecked_cast")
        return MyViewModel(app) as T
    }
}

