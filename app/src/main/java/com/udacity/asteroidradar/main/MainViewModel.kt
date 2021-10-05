package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class MainViewModel : ViewModel() {

    //internal
    private val _asteroidList = MutableLiveData<MutableList<Asteroid>>()
    // external - exposing as LiveData so that outer classes don't mess with the values
    val asteroidList: LiveData<MutableList<Asteroid>>
        get() = _asteroidList

    init {
        _asteroidList.value = ArrayList()
        _asteroidList.value = mutableListOf(Asteroid(543,"Big codename","12/13/2025",2.5,50000.0,2.3,44444.7,true))
    }
}

