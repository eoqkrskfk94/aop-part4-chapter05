package com.mj.aop_part4_chapter04.util

import aop.fastcampus.part4.chapter05.data.Url
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.mj.aop_part4_chapter04.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

object RetrofitUtil {

    val authApiService: AuthApiService by lazy { getGithubAuthRetrofit().create(AuthApiService::class.java) }

    private fun getGithubAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.GITHUB_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(buildOKHttpClient())
            .build()
    }

    private fun buildOKHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if(BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }



}