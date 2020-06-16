package com.example.sharedcode.network

import com.example.sharedcode.repo.NewsList
import io.ktor.client.HttpClient
import io.ktor.client.request.get

const val BASE_URL = "https://newsapi.org/v2/"
interface NewsApi {
    suspend fun getNewsSource(): NewsList
}

class NewsApiImpl(private val httpClient: HttpClient) : NewsApi {

    override suspend fun getNewsSource(): NewsList  =
        httpClient.get<NewsList>(
                "https://newsapi.org/v2/top-headlines?country=in&apiKey=1f5aa4d5dbd04f3e8d1c0cb592754dc5")

}