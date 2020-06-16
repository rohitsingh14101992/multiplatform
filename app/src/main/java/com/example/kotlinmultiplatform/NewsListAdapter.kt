package com.example.kotlinmultiplatform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedcode.repo.NewsArticles

class NewsListAdapter : RecyclerView.Adapter<NewsViewHolder>() {

    private val newsList = mutableListOf<NewsArticles>()

    fun updateList(list: List<NewsArticles>) {
        newsList.clear()
        newsList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_list_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int = newsList.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.updateView(newsList[position])
    }
}


class NewsViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {

    var titleTv = itemVIew.findViewById<TextView>(R.id.tv_title)
    var descTv = itemVIew.findViewById<TextView>(R.id.tv_desc)

    fun updateView(article: NewsArticles) {
        titleTv.text = article.title
        descTv.text = article.description
    }

}
