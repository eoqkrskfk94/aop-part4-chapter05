package com.mj.aop_part4_chapter04.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mj.aop_part4_chapter04.data.dao.RepositoryDao
import com.mj.aop_part4_chapter04.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {

    abstract fun repositoryDao(): RepositoryDao
}