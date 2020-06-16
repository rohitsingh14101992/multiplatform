package com.example.sharedcode.news

import com.example.sharedcode.base.Action
import com.example.sharedcode.base.Result
import com.example.sharedcode.base.State
import com.example.sharedcode.repo.NewsList

data class NewsListState(
    val loading: Boolean = false,
    val newsList: NewsList? = null,
    val error: String =  "",
    val showError: Boolean = false
) : State

sealed class NewsListAction : Action {
    object LoadNews : NewsListAction()
}

sealed class NewsListResult : Result {
    object Loading : NewsListResult()
    data class Result(val newsList: NewsList) : NewsListResult()
    data class Error(val error: String) : NewsListResult()
}