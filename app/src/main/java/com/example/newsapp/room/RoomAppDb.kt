package com.example.newsapp.room

import android.content.Context
import androidx.room.*
import com.example.newsapp.model.Article
import com.example.newsapp.model.Converters

@Database(entities = [Article::class], version = 2)
@TypeConverters(Converters::class)
abstract class RoomAppDb: RoomDatabase() {

    abstract fun userDao(): UserDao?

    companion object{
        private var INSTANCE: RoomAppDb?=null

        fun getAppDatabase(context: Context): RoomAppDb? {
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder<RoomAppDb>(
                    context.applicationContext,RoomAppDb::class.java,"appDB").
                        allowMainThreadQueries()
                        .build()
            }
            return INSTANCE
        }
    }
}