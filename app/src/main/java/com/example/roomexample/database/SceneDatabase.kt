package com.example.roomexample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Scene::class])
abstract class SceneDatabase : RoomDatabase() {
    abstract val sceneDatabaseDao: SceneDatabaseDao
    companion object {

        @Volatile
        private var INSTANCE: SceneDatabase? = null

        fun getInstance(context: Context): SceneDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SceneDatabase::class.java,
                        "scene_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    } 
}