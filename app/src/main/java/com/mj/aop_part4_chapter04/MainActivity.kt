package com.mj.aop_part4_chapter04

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import com.mj.aop_part4_chapter04.data.database.DataBaseProvider
import com.mj.aop_part4_chapter04.data.entity.GithubOwner
import com.mj.aop_part4_chapter04.data.entity.GithubRepoEntity
import com.mj.aop_part4_chapter04.databinding.ActivityMainBinding
import com.mj.aop_part4_chapter04.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    val job =  Job()

    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

    val repositoryDao by lazy { DataBaseProvider.provideDB(applicationContext).repositoryDao() }

    private lateinit var repositoryRecyclerAdapter: RepositoryRecyclerAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()

    }

    private fun initAdapter() {
        repositoryRecyclerAdapter = RepositoryRecyclerAdapter()
    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepos()
        }
    }

    private suspend fun loadLikedRepos() = withContext(Dispatchers.IO) {
        val repoList = DataBaseProvider.provideDB(this@MainActivity).repositoryDao().getHistory()

        withContext(Dispatchers.Main) {
            setData(repoList)
        }
    }

    private fun setData(githubRepoEntityList: List<GithubRepoEntity>) {
        if(githubRepoEntityList.isEmpty()) {
            binding.emptyResultTextView.isGone = false
            binding.recyclerView.isGone = true
        }else {
            binding.emptyResultTextView.isGone = true
            binding.recyclerView.isGone = false
            repositoryRecyclerAdapter.setSearchResultList(githubRepoEntityList) {
                startActivity(
                    Intent(this@MainActivity, RepositoryActivity::class.java).apply {
                        putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                        putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                    }
                )
            }
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = repositoryRecyclerAdapter
        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }
    }


}