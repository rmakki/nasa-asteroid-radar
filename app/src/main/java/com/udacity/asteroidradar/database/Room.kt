package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

// Data Access Object interface
@Dao
interface AsteroidDao {
    // Queries return LiveData so that UI can observe these queries
    // Room will do the database query in the background for us when we return LiveData

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

/**
 * Creates an abstract AsteroidsDatabase class that extends RoomDatabase,
 * Annotated with @Database, DatabaseAsteroid entity and version
  */
@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    // DB reference to the DAO
    abstract val asteroidDao: AsteroidDao

    companion object {

        // Singleton
        private lateinit var INSTANCE: AsteroidsDatabase

        fun getDatabase(context: Context): AsteroidsDatabase {
            // Thread safe
            synchronized(AsteroidsDatabase::class.java) {
                // Check whether the database has been initialized
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AsteroidsDatabase::class.java,
                        "asteroids").build()
                }
            }
            return INSTANCE
        }

    }
}


