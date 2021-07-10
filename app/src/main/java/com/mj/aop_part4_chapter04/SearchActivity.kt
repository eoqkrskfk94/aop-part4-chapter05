package com.mj.aop_part4_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.mj.aop_part4_chapter04.data.entity.GithubRepoEntity
import com.mj.aop_part4_chapter04.databinding.ActivitySearchBinding
import com.mj.aop_part4_chapter04.util.RetrofitUtil
import com.mj.aop_part4_chapter04.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
        bindViews()
    }

    private fun initAdapter() {
        adapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isGone = true
        recyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun searchKeyword(keywordString: String) = launch {
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.githubApiService.searchRepositories(keywordString)

            if(response.isSuccessful) {
                val body = response.body()
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        setData(body.items)
                    }
                }
            }
        }
    }

    private fun setData(items: List<GithubRepoEntity>) {
        adapter.setSearchResultList(items) {

        }
    }
}