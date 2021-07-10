package com.mj.aop_part4_chapter04.data.response

import com.mj.aop_part4_chapter04.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)
