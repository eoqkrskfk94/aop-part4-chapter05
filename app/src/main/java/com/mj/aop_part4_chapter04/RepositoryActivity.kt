package com.mj.aop_part4_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.mj.aop_part4_chapter04.data.database.DataBaseProvider
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

    private val repoDao by lazy { DataBaseProvider.provideDB(applicationContext).repositoryDao() }

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

                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        showLoading(false)
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

        setLikeState(githubRepoEntity)
    }

    private fun setLikeState(githubRepoEntity: GithubRepoEntity) = launch {
        withContext(Dispatchers.IO){
            val repo = repoDao.getRepository(githubRepoEntity.fullName)
            val isLike = repo != null
            withContext(Dispatchers.Main) {
                setLikeImage(isLike)
                binding.likeButton.setOnClickListener {
                    likeGithubRepository(githubRepoEntity, isLike)
                }
            }
        }
    }

    private fun setLikeImage(isLike: Boolean) {
        binding.likeButton.setImageDrawable(ContextCompat.getDrawable(this,
        if(isLike){
            R.drawable.ic_like
        }else{
            R.drawable.ic_dislike
        }))
    }

    private fun likeGithubRepository(githubRepoEntity: GithubRepoEntity, isLike: Boolean) = launch {
        withContext(Dispatchers.IO) {
            if(isLike) {
                repoDao.remove(githubRepoEntity.fullName)
            }else
                repoDao.insert(githubRepoEntity)
            withContext(Dispatchers.Main){
                setLikeImage(isLike.not())

            }
        }
    }


    private fun showLoading(isShown: Boolean) = with(binding) {
        progressBar.isGone = isShown.not()
    }
}