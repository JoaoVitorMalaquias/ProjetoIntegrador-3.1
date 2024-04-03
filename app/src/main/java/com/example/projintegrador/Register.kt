package com.example.projintegrador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityCalendarBinding
import com.example.projintegrador.databinding.ActivityRegisterBinding

class Register:AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
    
}