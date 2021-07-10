package com.mj.aop_part4_chapter04

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mj.aop_part4_chapter04.data.database.DataBaseProvider
import com.mj.aop_part4_chapter04.data.entity.GithubOwner
import com.mj.aop_part4_chapter04.data.entity.GithubRepoEntity
import com.mj.aop_part4_chapter04.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    val job =  Job()

    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

    val repositoryDao by lazy { DataBaseProvider.provideDB(applicationContext).repositoryDao() }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        launch {
            addMockData()
            val githubRepos = loadGithubRepos()

            withContext(coroutineContext){
                Log.e("repos", githubRepos.toString())
            }
        }
    }

    private fun initViews() = with(binding) {
        searchButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }
    }

    private suspend fun addMockData() = withContext(Dispatchers.IO) {
        val mockData = (0 until 10).map {
            GithubRepoEntity(
                name = "repo $it",
                fullName = "name $it",
                owner = GithubOwner(
                    "login",
                    "avaterUrl"
                ),
                description = null,
                language = null,
                updatedAt = Date().toString(),
                stargazersCount = it
            )
        }

        repositoryDao.insertAll(mockData)
    }

    private suspend fun loadGithubRepos() = withContext(Dispatchers.IO) {
        val repos = repositoryDao.getHistory()
        return@withContext repos
    }
}