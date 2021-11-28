package com.example.newsapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.newsapp.R
import com.example.newsapp.WebViewActivity
import com.example.newsapp.model.Article

class NewsAdapter(val context: Context): RecyclerView.Adapter<NewsViewHolder>()
{
    var articles : List<Article> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item,parent,false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
//        holder.itemTitle.text = articles[position].title
        holder.itemDescription.text = articles[position].description
        holder.itemPublishedAt.text = articles[position].publishedAt
        Glide.with(holder.itemView.context)
            .load(articles[position].urlToImage)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(CenterInside(), RoundedCorners(5))
            .into(holder.itemImage)

        holder.itemView.setOnClickListener(View.OnClickListener { v ->
            var intent = Intent(context?.applicationContext, WebViewActivity::class.java)
            intent.putExtra("url",articles[position].url)
            startActivity(context?.applicationContext,intent,null)


        })


    }

    override fun getItemCount(): Int {
        return articles.size
    }
    fun setArticlesList(articleList: List<Article>){
        this.articles = articleList;
        notifyDataSetChanged()
    }
}

class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
     val itemImage:ImageView = itemView.findViewById(R.id.itemImage)
//     val itemTitle:TextView = itemView.findViewById(R.id.itemTitle);
     val itemDescription:TextView = itemView.findViewById(R.id.itemDescription);
     val itemPublishedAt:TextView = itemView.findViewById(R.id.itemPublishedAt);


}
