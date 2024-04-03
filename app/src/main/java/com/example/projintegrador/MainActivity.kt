package com.example.projintegrador

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projintegrador.databinding.ActivityEscolherServicoBinding
import com.example.projintegrador.databinding.ActivityMainBinding
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
            val NavigateToCalendar = Intent(this, CalendarView::class.java)
            startActivity(NavigateToCalendar)
        }

        binding.btnRegistrarPonto.setOnClickListener {
            val NavigateToRegister = Intent(this, Register::class.java)
            startActivity(NavigateToRegister)
        }

        binding.btnRelatorio.setOnClickListener {
            val NavigateToRelatory = Intent(this, Relatory::class.java)
            startActivity(NavigateToRelatory)
        }


    }
}