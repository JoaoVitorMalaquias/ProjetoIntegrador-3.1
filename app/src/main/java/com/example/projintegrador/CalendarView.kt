package com.example.projintegrador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityCalendarBinding
import com.example.projintegrador.databinding.ActivityMainBinding

class CalendarView: AppCompatActivity() {

    private lateinit var binding:ActivityCalendarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}