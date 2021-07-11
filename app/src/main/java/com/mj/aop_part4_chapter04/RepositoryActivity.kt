package com.mj.aop_part4_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.mj.aop_part4_chapter04.data.entity.GithubRepoEntity
import com.mj.aop_part4_chapter04.databinding.ActivityRepositoryBinding
import com.mj.aop_part4_chapter04.extensions.loadCenterInside
import com.mj.aop_part4_chapter04.util.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {

    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityRepositoryBinding

    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repoOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            finish()
            return
        }

        val repoName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            finish()
            return
        }

        launch {
            loadRepository(repoOwner, repoName)?.let {
                setData(it)
            } ?: run {
                finish()
            }
        }

        showLoading(true)
    }

    private suspend fun loadRepository(repositoryOwner: String, repositoryName: String): GithubRepoEntity? =
        withContext(coroutineContext) {
            var repoEntity: GithubRepoEntity? = null
            withContext(Dispatchers.IO) {
                val response = RetrofitUtil.githubApiService.getRepository(
                    ownerLogin = repositoryOwner,
                    repoName = repositoryName
                )

                if(response.isSuccessful) {
                    showLoading(false)
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        body?.let { repo ->
                            repoEntity = repo
                        }
                    }
                }
            }
            repoEntity
        }

    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding) {
        ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        ownerNameAndRepoNameTextView.text = "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
        stargazersCountText.text = githubRepoEntity.stargazersCount.toString()
        githubRepoEntity.language?.let { language ->
            languageText.isGone = false
            languageText.text = language
        } ?: kotlin.run {
            languageText.isGone = true
            languageText.text = ""
        }
        descriptionTextView.text = githubRepoEntity.description
        updateTimeTextView.text = githubRepoEntity.updatedAt
    }


    private fun showLoading(isShown: Boolean) = with(binding) {
        progressBar.isGone = isShown.not()
    }
}