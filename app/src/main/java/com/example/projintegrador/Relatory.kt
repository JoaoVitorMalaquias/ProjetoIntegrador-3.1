package com.example.projintegrador

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projintegrador.databinding.ActivityCalendarBinding
import com.example.projintegrador.databinding.ActivityRelatoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class Relatory: AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase

    private lateinit var binding: ActivityRelatoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        database = FirebaseDatabase.getInstance()

        userId = getUserId()

        userId = FirebaseAuth.getInstance().currentUser?.email?.replace(".", "_") ?: ""



        exibirRelatorioHorasTrabalhadas()

        binding.imageButton.setOnClickListener{
            val navToMain = Intent(this, MainActivity::class.java)
            startActivity(navToMain)
        }


    }

    private fun exibirRelatorioHorasTrabalhadas() {
        val diaDaSemana = getDiaDaSemanaAtual()
        val referencia = FirebaseDatabase.getInstance().getReference("RegistrarPonto").child(userId).child(diaDaSemana)

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var horasTrabalhadasMillis: Long = 0

                // Itera sobre os registros para calcular o total de horas trabalhadas
                for (registro in snapshot.children) {
                    val tipo = registro.child("tipo").getValue(String::class.java)
                    val pontoEntrada = registro.child("ponto").getValue(String::class.java)
                    val pontoSaida = registro.child("pontoSaida").getValue(String::class.java)

                    if (tipo == "entrada" && pontoSaida != null) {
                        // Converte os pontos de entrada e saída em milissegundos desde a meia-noite
                        val horaEntradaMillis = converterHoraParaMillis(pontoEntrada!!)
                        val horaSaidaMillis = converterHoraParaMillis(pontoSaida)

                        // Calcula a diferença entre os pontos de entrada e saída em milissegundos
                        horasTrabalhadasMillis += horaSaidaMillis - horaEntradaMillis
                    }
                }

                // Converte as horas trabalhadas de millisegundos para horas, minutos e segundos
                val horasTrabalhadas = horasTrabalhadasMillis / (1000 * 60 * 60)
                val minutosTrabalhados = (horasTrabalhadasMillis % (1000 * 60 * 60)) / (1000 * 60)
                val segundosTrabalhados = ((horasTrabalhadasMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000

                // Concatena as horas trabalhadas ao texto "Horas Trabalhadas"
                val reportText = "Horas Trabalhadas: $horasTrabalhadas horas, $minutosTrabalhados minutos, $segundosTrabalhados segundos"
                binding.txtHoursWorked.text = reportText
            }

            override fun onCancelled(error: DatabaseError) {
                // Trate o erro, se necessário
            }
        })
    }

    private fun converterHoraParaMillis(hora: String): Long {
        val parts = hora.split(":")
        val horaInt = parts[0].toInt()
        val minutoInt = parts[1].toInt()
        // Calcula os milissegundos desde a meia-noite
        return (horaInt * 60 * 60 * 1000 + minutoInt * 60 * 1000).toLong()
    }

    private fun getUserId(): String {
        // Obtém o usuário atualmente autenticado
        val user = FirebaseAuth.getInstance().currentUser
        // Verifica se o usuário está autenticado e se o e-mail está disponível
        val userEmail = user?.email ?: throw IllegalStateException("Usuário não autenticado ou e-mail indisponível")
        // Substitui caracteres inválidos do e-mail por underscore ("_")
        return userEmail.replace(".", "_")
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

}


