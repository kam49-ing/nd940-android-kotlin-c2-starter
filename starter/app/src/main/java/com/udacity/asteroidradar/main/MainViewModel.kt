package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidDatabaseDao
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate

class MainViewModel(var database: AsteroidDatabaseDao, application: Application): AndroidViewModel(application) {
//    enum class AsteroidStatus{LOADING, DONE,ERROR}
    var today = LocalDate.now().toString()

    private var _asteroids = MutableLiveData<List<Asteroid?>>()
    val asteroids: LiveData<List<Asteroid>?> = database.getAsteroidsByDate(today)
    private var _navigateToAsteroidDetail=MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetail:LiveData<Asteroid?>
        get() = _navigateToAsteroidDetail
    private var _dayImageUrl = MutableLiveData<String>()
    val dayImageUrl:LiveData<String>
        get() = _dayImageUrl


    /*private val _status = MutableLiveData<AsteroidStatus>()
    val status: LiveData<AsteroidStatus>
    get()=_status*/
    init {
        _navigateToAsteroidDetail.value = null
        getDayImage()
    }



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


    fun getAsteroidProperties(){
        viewModelScope.launch {
            try {

//                _status.value = AsteroidStatus.LOADING
                val response = AsteroidApi.retrofitService.getAsteroids()
                try {
                    val asteroidJson = JSONObject(response)
                    val asteroidList = parseAsteroidsJsonResult(asteroidJson)

                    for (asteroid in asteroidList){
                        try {
                            database.insert(asteroid)
                        }catch (e:Exception){
                        }
                    }

                }catch (e:Exception){
//                    _status.value = AsteroidStatus.ERROR
                }
            }catch (e:Exception){
//                 _status.value = AsteroidStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
    }

    fun getDayImage(){
        viewModelScope.launch {
            try {
                val dayImage = AsteroidApi.retrofitService.getDayImage()
                val day = JSONObject(dayImage).getString("url")
                _dayImageUrl.value = day
            }catch (e:Exception){
                throw java.lang.Exception("Error: "+e)
            }
        }

    }

}


