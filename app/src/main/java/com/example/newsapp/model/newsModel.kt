package com.example.newsapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

private val klaxon = Klaxon()

data class newsModel (
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Long,
    @SerializedName("articles")
    val articles: List<Article>
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<newsModel>(json)
    }
}
@Entity(tableName = "userNews")
data class Article (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id") val id : Int =0,

    @ColumnInfo(name = "source")
    @SerializedName("source")
    val source: Source?,

    @ColumnInfo(name = "author")
    @SerializedName("author")
    val author: String? = null,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String?,

    @ColumnInfo(name = "url")
    @SerializedName("url")
    val url: String?,

    @ColumnInfo(name = "urlToImage")
    @SerializedName("urlToImage")
    val urlToImage: String?,

    @ColumnInfo(name = "publishedAt")
    @SerializedName("publishedAt")
    val publishedAt: String?,

    @ColumnInfo(name = "content")
    @SerializedName("content")
    val content: String? = null
)

data class Source (
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)

class Converters {

    @TypeConverter
    fun listToJsonString(value: Source?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Source::class.java)
}
