package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.network.AsteroidApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackbar()`.
     */
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the user rotates their phone it won't show a duplicate
     * toast.
     */

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * Variable that tells the Fragment to navigate to [AsteroidFragment]
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     * If this is non-null, immediately navigate to [AsteroidFragment] and call [onAsteroidDetailsClicked]

     */

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetails: LiveData<Asteroid>
        get() = _navigateToAsteroidDetails

    // Triggers navigation to AsteroidDetails
    fun onAsteroidDetailsClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    /**
     * Call this immediately after navigating to [AsteroidFragment]
     *
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigatingToAsteroidDetails() {
        _navigateToAsteroidDetails.value = null
    }

    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList


    init {

        getAsteroids()
        // Hardcoded for now until I retrieve from Network and convert from Json to Object successfully
        _asteroidList.value = ArrayList()
        _asteroidList.value = mutableListOf(Asteroid(543,"Big codename","12/13/2025",2.5,50000.0,2.3,44444.7,true))
    }

    /**
     * Retrieve the Asteroid List via network and set the Livedata
     */
    private fun getAsteroids() {
        AsteroidApi.retrofitService.getAsteroids("2021-10-20","2021-10-27","3Ece5JvM6wnEUGZP8Xn1sWlNg1q1cZPdSwBvAFij").enqueue( object:
            Callback<List<Asteroid>> {
            override fun onFailure(call: Call<List<Asteroid>>, t: Throwable) {
                Log.e("Network Failure : ",  t.stackTraceToString())
            }

            override fun onResponse(call: Call<List<Asteroid>>, response: Response<List<Asteroid>>) {
                Log.i("Asteroids size retrieved: ",  response.body()?.size.toString())
                //Log.i("Asteroids : ",  response.body().toString())
            }
        })
    }

    /*
    private val _navigateToAsteroidDetails = MutableLiveData<Long>()
    val navigateToAsteroidDetails
        get() = _navigateToAsteroidDetails

    fun onAsteroidDetailsClicked(id: Long) {
        _navigateToAsteroidDetails.value = id
    }

    fun onAsteroidDetailsNavigated() {
        _navigateToAsteroidDetails.value = null
    }
**/
}

