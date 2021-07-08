package com.mj.aop_part4_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mj.aop_part4_chapter04.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}