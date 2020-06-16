package com.example.sharedcode.repo

import kotlinx.serialization.Serializable

@Serializable
data class NewsList(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticles>
)
@Serializable
data class NewsArticles(
    val author: String?,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String
)