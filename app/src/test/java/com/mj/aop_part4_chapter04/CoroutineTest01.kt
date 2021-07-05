package com.mj.aop_part4_chapter04

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineTest01 {

    @Test
    fun test01() = runBlocking {

        val time = measureTimeMillis {
            val name = getFirstName()
            val lastName = getLastName()

            println("$name $lastName")
        }

        println(time)

    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis {
            val name = async { getFirstName() }
            val lastName = async { getLastName() }

            println("${name.await()} ${lastName.await()}")
        }

        println(time)
    }

    suspend fun getFirstName(): String {
        delay(1000)
        return "mj"
    }

    suspend fun getLastName(): String {
        delay(1000)
        return "kim"
    }
}