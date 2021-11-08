package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate

class MainViewModel(var database: AsteroidDatabaseDao, application: Application): AndroidViewModel(application) {
//    enum class AsteroidStatus{LOADING, DONE,ERROR}
    private var _navigateToAsteroidDetail=MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetail:LiveData<Asteroid?>
        get() = _navigateToAsteroidDetail
    private var _dayImageUrl = MutableLiveData<String>()
    private val asteroidRepository = AsteroidRepository(database)


    val dayImageUrl:LiveData<String>
        get() = _dayImageUrl




    /*private val _status = MutableLiveData<AsteroidStatus>()
    val status: LiveData<AsteroidStatus>
    get()=_status*/
    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroid()
        }
        _navigateToAsteroidDetail.value = null
        getDayImageUrl()
    }
    val asteroids = asteroidRepository.asteroids

    fun addAsteroid(asteroid: Asteroid){
        viewModelScope.launch {
            addAsteroidToDatabase(asteroid)
        }
    }

    private suspend fun addAsteroidToDatabase(asteroid: Asteroid){
        database.insert(asteroid)
    }

    suspend fun getAsteroidFromDatabase(asteroidId: Long): Asteroid? {
        return database.getAsteroid(asteroidId)
    }
    fun onAsteroidClicked(asteroidId:Long){
        viewModelScope.launch {
            _navigateToAsteroidDetail.value = getAsteroidFromDatabase(asteroidId)
        }
    }


    fun onAsteroidDetailNavigated(){
        _navigateToAsteroidDetail.value = null
    }


    private fun getDayImageUrl(){
        viewModelScope.launch {
            try {
                val dayImage = AsteroidApi.retrofitService.getDayImage()
                val day = JSONObject(dayImage).getString("url")
                _dayImageUrl.value = day
            }catch (e:Exception){

            }
        }

    }

}


