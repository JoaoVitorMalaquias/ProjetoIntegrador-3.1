package com.example.projintegrador

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityCadastrarUsuarioBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth

class CadastrarUsuario : AppCompatActivity() {
    private  var binding: ActivityCadastrarUsuarioBinding?=null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastrarUsuarioBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        auth = Firebase.auth

        binding?.btnCadastrar?.setOnClickListener {
            val email = binding?.editUser?.text.toString()
            val password = binding?.editPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                createUserWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(
                    this@CadastrarUsuario,
                    "Por favor, preencha os campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding?.btnVoltar?.setOnClickListener {
            val navToVerificandoLogin = Intent(this, VerificandoLogin::class.java)
            startActivity(navToVerificandoLogin)
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmailAndPassword::Sucess")
                val user = auth.currentUser
                val navToMainActivity = Intent(this, VerificandoLogin::class.java)
                startActivity(navToMainActivity)

            } else {
                Log.w(TAG, "createUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Não foi possível realizar o cadastro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object{
        private var TAG = "EmailAndPassword"
    }


    override fun onDestroy(){
        super.onDestroy()
        binding = null
    }

}
