package com.example.kotlinmultiplatform

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedcode.news.NewsListState
import com.example.sharedcode.news.NewsListViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: NewsListViewModel = NewsListViewModel.create()
    private val newsListAdapter by lazy { NewsListAdapter() }
    private val newsListRv by lazy {
        findViewById<RecyclerView>(R.id.rv_news_list)
    }

    private val errorTextView by lazy {
        findViewById<TextView>(R.id.tv_error_msg)
    }

    private val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progress_circular)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        newsListRv.adapter = newsListAdapter
        viewModel.observeState().asLiveData().observe(this,
            Observer<NewsListState> {
                when {
                    it.loading -> {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        newsListRv.visibility = View.GONE
                    }
                    it.showError -> {
                        progressBar.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = it.error
                        newsListRv.visibility = View.GONE
                    }
                    else -> {
                        progressBar.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                        newsListRv.visibility = View.VISIBLE
                        it.newsList?.let { newsList ->
                            newsListAdapter.updateList(newsList.articles)
                        }

                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
}