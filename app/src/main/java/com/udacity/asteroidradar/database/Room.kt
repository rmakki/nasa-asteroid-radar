package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Data Access Object interface
@Dao
interface AsteroidDAO {

    // fetch all asteroids from DB
    @Query("select * from asteroid_table order by closeApproachDate asc")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    // fetch coming asteroids Today
    @Query("select * from asteroid_table where closeApproachDate = :dateToday order by closeApproachDate asc")
    fun getAsteroidsToday(dateToday: String): LiveData<List<DatabaseAsteroid>>

    // fetch asteroids from DB after a certain date
    @Query("select * from asteroid_table where closeApproachDate >= :dateStart order by closeApproachDate asc")
    fun getAsteroidsAfterDate(dateStart: String): LiveData<List<DatabaseAsteroid>>

    // insert asteroids/overwrites old entry
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

}