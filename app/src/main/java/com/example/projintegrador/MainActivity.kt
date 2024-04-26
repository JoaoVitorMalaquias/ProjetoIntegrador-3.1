package com.example.projintegrador

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityEscolherServicoBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEscolherServicoBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscolherServicoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnCalendarioSemanal.setOnClickListener {
            val navigateToCalendar = Intent(this, CalendarView::class.java)
            startActivity(navigateToCalendar)
        }

        binding.btnRegistrarPonto.setOnClickListener {
            val navigateToRegister = Intent(this, Register::class.java)
            startActivity(navigateToRegister)
        }

        binding.btnRelatorio.setOnClickListener {
            val navigateToRelatory = Intent(this, Relatory::class.java)
            startActivity(navigateToRelatory)
        }



    }
}