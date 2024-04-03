package com.example.projintegrador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityCalendarBinding
import com.example.projintegrador.databinding.ActivityRelatoryBinding

class Relatory: AppCompatActivity() {
    private lateinit var binding: ActivityRelatoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

}