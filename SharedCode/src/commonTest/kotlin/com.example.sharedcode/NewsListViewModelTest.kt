package com.example.sharedcode

import com.example.sharedcode.base.Lce
import com.example.sharedcode.news.NewsListAction
import com.example.sharedcode.news.NewsListState
import com.example.sharedcode.news.NewsListViewModel
import com.example.sharedcode.repo.NewsArticles
import com.example.sharedcode.repo.NewsList
import com.example.sharedcode.repo.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlin.test.*


class NewsListViewModelTest {

    lateinit var newsListViewModel: NewsListViewModel
    val repoBroadcastChannel : ConflatedBroadcastChannel<Lce<NewsList>> = ConflatedBroadcastChannel()

    val newsListRepository = object : NewsRepository {
        override suspend fun getNewsSource(): Flow<Lce<NewsList>> = repoBroadcastChannel.asFlow()
    }

    val newsArticles = listOf(NewsArticles(author = "author", title = "title", description = "desc",
    url = "url", publishedAt = "date", urlToImage = ""))
    val newsList = NewsList(articles = newsArticles, status = "ok", totalResults = 1)

    @BeforeTest
    fun setUp() {
        newsListViewModel = NewsListViewModel(NewsListState(), newsListRepository, Dispatchers.Unconfined, Dispatchers.Unconfined)
    }

    @Test
    fun `test action to results`() =
        runTest {

            newsListViewModel.sendEvent(NewsListAction.LoadNews)

            repoBroadcastChannel.offer(Lce.Content(newsList))

            val stateValue = newsListViewModel.observeState().first()
            assertEquals(newsList, stateValue.newsList)
        }

}