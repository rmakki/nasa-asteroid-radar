package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch


/**
 * MainViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

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
     * Variable that tells the Fragment to navigate to [.detail.DetailFragment]
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     * If this is non-null, immediately navigate to [.detail.DetailFragment] and call [onAsteroidDetailsClicked]

     */

    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetails: LiveData<Asteroid>
        get() = _navigateToAsteroidDetails

    // Triggers navigation to AsteroidDetails
    fun onAsteroidDetailsClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    /**
     * Call this immediately after navigating to [.detail.DetailFragment]
     *
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigatingToAsteroidDetails() {
        _navigateToAsteroidDetails.value = null
    }

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        viewModelScope.launch {
            // refresh astroidList from net
            asteroidsRepository.refreshAsteroids()
            // refresh apod  from net
            asteroidsRepository.refreshPictureOfTheDay()
        }


    }
   // Filter initialized to one week from today
    var filter: MutableLiveData<String> = MutableLiveData(Constants.FILTER_WEEK)

    /**
     * LiveData to AsteroidList, displays Asteroids from Today onwards (from the cache)as default when user opens the app
     * Every time user selects from Menu, lambda function is triggered and asteroidList is updated
     * Since asteroidList is a LiveData, then observers of the asteroidList- UI/RV - gets updated
    */
    val asteroidList: LiveData<List<Asteroid>> = Transformations.switchMap(filter) {  it ->
        when (it) {
            Constants.FILTER_TODAY -> asteroidsRepository.getAsteroidsApproachingToday()
            Constants.FILTER_WEEK  -> asteroidsRepository.getAsteroidsStartToday()
            else -> asteroidsRepository.getAsteroidsAll()
        }
    }

    // Astronomy Picture of the Day APOD LiveData
    //private val _apod= MutableLiveData<PictureOfDay?>() //moved to Repo
    val apod: LiveData<PictureOfDay> = asteroidsRepository.apod

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}

