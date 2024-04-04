package com.example.projintegrador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityEscolherServicoBinding
import com.example.projintegrador.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class VerificandoLogin: AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        //inicializando a variavel
        auth = Firebase.auth

        binding?.btnLogin?.setOnClickListener {
            val email = binding?.editUser?.text.toString()
            val password = binding?.editPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(
                    this@VerificandoLogin,
                    "Por favor, preencha os campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    //Funcao para criar um usuario, porém não sera utilizada
    //private fun createUserWithEmailAndPassword(email:String, password:String){
    //auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
    //if (task.isSuccessful){
    //Log.d(TAG, "createUserWithEmailAndPassword::Sucess")
    //val user = auth.currentUser //pegar informação do usuario
    //}else{
    //Log.w(TAG, "createUserWithEmailAndPassword:Failure", task.exception)
    //Toast.makeText(baseContext,"Não foi possível realizar o login", Toast.LENGTH_SHORT).show()
    //}
    //}


    //Fazendo login do Usuario
    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmailAndPassword:Success")
                Toast.makeText(this,"Login Efetuado com Sucesso", Toast.LENGTH_SHORT).show()
                val navigateToActivityMain = Intent(this, MainActivity::class.java)
                startActivity(navigateToActivityMain)
                //val user = auth.currentUser
            } else {
                Log.w(TAG, "signInUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Não foi possivel realizar o login", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    //é uma função que todos podem acessar.!!!!
    companion object {
        private var TAG = "EmailAndPassword"
    }


    /*
    quando essa aplicação for destruida, aqui garante que o binding volta a ser nulo,
    pois ele em cima inicia como nulo
    QUESTÃO DE PERFANCE.
     */
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}