package com.example.sharedcode.news

import com.example.sharedcode.base.Lce
import com.example.sharedcode.base.ViewModel
import com.example.sharedcode.di.kodein
import com.example.sharedcode.ioDispatcher
import com.example.sharedcode.repo.NewsRepository
import com.example.sharedcode.uiDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.kodein.di.erased.instance
import kotlin.native.concurrent.ThreadLocal


class NewsListViewModel(
    state: NewsListState,
    val newsRepository: NewsRepository,
    private val mainCoroutineDispatcher: CoroutineDispatcher,
    private val ioCoroutineDispatcher: CoroutineDispatcher
) :
    ViewModel<NewsListAction, NewsListState, NewsListResult>(state, mainCoroutineDispatcher) {


    init {
        sendEvent(NewsListAction.LoadNews)
    }

    override suspend fun actionToResults(action: NewsListAction): Flow<NewsListResult> {
        return when (action) {
            is NewsListAction.LoadNews ->
                newsRepository.getNewsSource().map {
                    when (it) {
                        is Lce.Loading -> NewsListResult.Loading

                        is Lce.Content -> NewsListResult.Result(it.packet)

                        else -> NewsListResult.Error("Something went wrong")
                    }
                }.flowOn(ioCoroutineDispatcher)
        }
    }

    override suspend fun resultToState(
        result: NewsListResult,
        state: NewsListState
    ): NewsListState {
        return when (result) {
            is NewsListResult.Loading -> state.copy(loading = true, showError = false)

            is NewsListResult.Result -> state.copy(
                loading = false,
                newsList = result.newsList,
                showError = false
            )

            is NewsListResult.Error -> state.copy(
                loading = false,
                error = result.error,
                showError = true
            )
        }
    }

    @ThreadLocal
    companion object {
        fun create(): NewsListViewModel {
            val newsRepository: NewsRepository by kodein.instance<NewsRepository>()
            return NewsListViewModel(
                NewsListState(),
                newsRepository,
                uiDispatcher(),
                ioDispatcher()
            )
        }
    }

}