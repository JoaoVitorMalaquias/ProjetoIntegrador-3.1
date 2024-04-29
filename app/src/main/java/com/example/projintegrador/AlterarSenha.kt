package com.example.projintegrador

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projintegrador.databinding.ActivityAlterarSenhaBinding
import com.example.projintegrador.databinding.ActivityRegistrarPontoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AlterarSenha : AppCompatActivity() {
    private var binding: ActivityAlterarSenhaBinding? = null
    private lateinit var database: FirebaseDatabase
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlterarSenhaBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        database = Firebase.database

        binding?.btnVoltar?.setOnClickListener {
            val navToVerificandoLogin = Intent(this, VerificandoLogin::class.java)
            startActivity(navToVerificandoLogin)
        }

        binding?.btnAlterarSenha?.setOnClickListener {
            val email = binding?.editUser?.text.toString()

            if (email.isNotEmpty()) {
                mandaEmail(email)
            } else {
                Toast.makeText(
                    this@AlterarSenha,
                    "Por favor, preencha os campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mandaEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email Enviado com Sucesso", Toast.LENGTH_SHORT).show()


                    val navToVerificandoLogin = Intent(this, VerificandoLogin::class.java)
                    startActivity(navToVerificandoLogin)
                } else {
                    Log.w(TAG, "sendPasswordResetEmail:Failure", task.exception)
                    Toast.makeText(baseContext, "Não foi possivel enviar o email", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

}