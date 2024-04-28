package com.example.projintegrador

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityRegistrarPontoBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class Register : AppCompatActivity() {

    private var binding: ActivityRegistrarPontoBinding? = null
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarPontoBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        database = Firebase.database

        binding?.btnResgistrarPontoEntrada?.setOnClickListener {
            registrarPontoEntrada()
        }

        binding?.btnResgistrarPontoSaida?.setOnClickListener {
            registrarPontoSaida()
        }
    }

    private fun registrarPontoEntrada() {
        val timestamp = System.currentTimeMillis()
        val horaAtual = timestamp.toString()
        val diaDaSemana = getDiaDaSemanaAtual()

        val referencia = database.getReference("RegistrarPonto")
        referencia.child("pontoEntrada").setValue(horaAtual)
            .addOnSuccessListener {
                // Salvar o dia da semana também
                referencia.child("diaDaSemana").setValue(diaDaSemana)
                binding?.txtResgistrarPontoEntrada?.text = "Ponto de entrada registrado: $horaAtual"
                Toast.makeText(this, "Ponto de entrada registrado com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao registrar ponto de entrada: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registrarPontoSaida() {
        val referencia = database.getReference("RegistrarPonto")

        // Verifica se o ponto de entrada foi registrado
        referencia.child("pontoEntrada").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Ponto de entrada registrado, agora registre o ponto de saída
                val timestamp = System.currentTimeMillis()
                val horaAtual = timestamp.toString()
                val diaDaSemana = getDiaDaSemanaAtual()

                referencia.child("pontoSaida").setValue(horaAtual)
                    .addOnSuccessListener {
                        // Salvar o dia da semana também
                        referencia.child("diaDaSemana").setValue(diaDaSemana)
                        binding?.txtResgistrarPontoSaida?.text = "Ponto de saída registrado: $horaAtual"
                        Toast.makeText(this, "Ponto de saída registrado com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Falha ao registrar ponto de saída: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Ponto de entrada não registrado
                Toast.makeText(this, "Registre o ponto de entrada primeiro", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao verificar o ponto de entrada: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getDiaDaSemanaAtual(): String {
        val cal = Calendar.getInstance()
        val diaDaSemana = cal.get(Calendar.DAY_OF_WEEK)
        return when (diaDaSemana) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Segunda-feira"
            Calendar.TUESDAY -> "Terça-feira"
            Calendar.WEDNESDAY -> "Quarta-feira"
            Calendar.THURSDAY -> "Quinta-feira"
            Calendar.FRIDAY -> "Sexta-feira"
            Calendar.SATURDAY -> "Sábado"
            else -> ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
