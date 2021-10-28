package com.udacity.asteroidradar

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AsteroidViewModelFactory(val databaseDao: AsteroidDatabaseDao,
                               private val application: Application):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AsteroidViewModel::class.java))
            return AsteroidViewModel(databaseDao, application) as T
        throw IllegalArgumentException("unknown view model class")
    }

}