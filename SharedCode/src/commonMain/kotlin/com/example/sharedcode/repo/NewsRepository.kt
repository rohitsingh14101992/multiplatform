package com.example.sharedcode.repo

import com.example.sharedcode.base.Lce
import com.example.sharedcode.network.NewsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface NewsRepository {
    suspend fun getNewsSource(): Flow<Lce<NewsList>>
}

class NewsListRepositoryImpl(private val newsApi: NewsApi) : NewsRepository {

    override suspend fun getNewsSource(): Flow<Lce<NewsList>>  =  flow {
        emit(Lce.Loading<NewsList>())
        emit(Lce.Content(newsApi.getNewsSource()))
    }.catch {
        emit(Lce.Error(it))
    }
}