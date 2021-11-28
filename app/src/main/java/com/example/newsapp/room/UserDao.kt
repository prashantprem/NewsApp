package com.example.newsapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.newsapp.model.Article

@Dao
interface UserDao {
    @Query("SELECT * FROM userNews ORDER BY id ASC")
    fun getAllNews(): LiveData<List<Article>>

    @Insert
    fun insertNews(article: List<Article>?)

    @Query("DELETE FROM userNews")
    fun delete()

}