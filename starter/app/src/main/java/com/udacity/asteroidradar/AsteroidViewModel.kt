package com.udacity.asteroidradar

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class AsteroidViewModel( var database: AsteroidDatabaseDao, application: Application): AndroidViewModel(application) {
    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids
    init {
        getAllAsteroid()
    }
    private fun getAllAsteroid(){
        viewModelScope.launch {
            val asteroidsList = getAsteroidsFromDatabase()
            if (asteroidsList!=null)
                _asteroids.value = asteroidsList
        }

    }

    private suspend fun getAsteroidsFromDatabase(): List<Asteroid>? {
        return database.getAllAsteroids()
    }

//    fun addAsteroid(asteroid:Asteroid){
//        viewModelScope.launch {
//            addAsteroidToDatabase(asteroid)
//        }
//    }
    private suspend fun addAsteroidToDatabase(asteroid: Asteroid){
        database.insert(asteroid)
    }

}