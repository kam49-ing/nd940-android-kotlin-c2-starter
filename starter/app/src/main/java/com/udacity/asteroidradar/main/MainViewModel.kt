package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(var database: AsteroidDatabaseDao, application: Application): AndroidViewModel(application) {
    enum class AsteroidStatus{LOADING,ERROR, DONE }
    private var _navigateToAsteroidDetail=MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetail:LiveData<Asteroid?>
        get() = _navigateToAsteroidDetail
    private var _dayImageUrl = MutableLiveData<String>()
    private val asteroidRepository = AsteroidRepository(database)




    val dayImageUrl:LiveData<String>
        get() = _dayImageUrl
    enum class OptionMenu { SHOW_ALL, SHOW_TODAY, SHOW_WEEK }
    private val _status = MutableLiveData<AsteroidStatus>()
    val status: LiveData<AsteroidStatus>
    get()=_status
    init {
        _status.value = AsteroidStatus.LOADING
        viewModelScope.launch {
            asteroidRepository.refreshAsteroid()
        }
        _navigateToAsteroidDetail.value = null
        getDayImageUrl()
    }

    var asteroids:LiveData<List<Asteroid>?> = asteroidRepository.asteroidsOfWeek
    var asteroidWithFilter:LiveData<List<Asteroid>?>? = null

    fun getAsteroid(optionMenu: OptionMenu) {
        asteroidWithFilter = when(optionMenu){
            OptionMenu.SHOW_ALL -> asteroidRepository.allAsteroids
            OptionMenu.SHOW_TODAY->asteroidRepository.asteroidOfToday
            else->asteroidRepository.asteroidsOfWeek
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
        try {
            viewModelScope.launch {
                try {
                    val dayImage = AsteroidApi.retrofitService.getDayImage()
                    val day = JSONObject(dayImage).getString("url")
                    _dayImageUrl.value = day
                } catch (e: Exception) {
                    _status.value =  AsteroidStatus.ERROR
                }
            }
        }catch(e:Exception){
            _status.value = AsteroidStatus.ERROR
        }

    }

    //put status to null when snackbar is already showed
    fun onSnackBarShowed(){
        _status.value = null
    }

}


