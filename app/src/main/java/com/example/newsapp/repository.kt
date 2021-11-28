package com.example.newsapp

import android.content.Context
import android.content.Entity
import androidx.lifecycle.LiveData
import com.example.newsapp.model.Article
import com.example.newsapp.room.RoomAppDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class repository(private val db: RoomAppDb) {
    companion object{

        var articleDatabase: RoomAppDb? = null

        var newsArticleModel: LiveData<List<Article>>? = null

        fun initializeDB(context: Context) : RoomAppDb {
            return RoomAppDb.getAppDatabase(context)!!
        }

        fun insertData(context: Context, entity: List<Article>) {

            articleDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                articleDatabase!!.userDao()?.insertNews(entity)
            }

        }
        fun getArticles(context: Context) : LiveData<List<Article>>? {

            articleDatabase = initializeDB(context)

            newsArticleModel = articleDatabase!!.userDao()?.getAllNews()

            return newsArticleModel
        }

        fun delete(context: Context){
            articleDatabase = initializeDB(context)
            articleDatabase!!.userDao()?.delete()
        }

    }





}