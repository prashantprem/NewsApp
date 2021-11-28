package com.example.newsapp

import android.app.Application
import android.content.Context
import android.content.Entity
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.model.Article
import com.example.newsapp.room.RoomAppDb

class ArticleViewModel: ViewModel() {

    var LiveArticleData: LiveData<List<Article>>?=null

    fun insertData(context: Context, entity: List<Article>) {
       repository.insertData(context,entity)
    }

    fun getArticleDetails(context: Context) : LiveData<List<Article>>? {
        LiveArticleData = repository.getArticles(context)
        return LiveArticleData
    }

    fun delete(context: Context){
     repository.delete(context)
    }



}