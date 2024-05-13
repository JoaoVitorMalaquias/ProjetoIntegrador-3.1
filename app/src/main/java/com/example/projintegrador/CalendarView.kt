package com.example.projintegrador

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projintegrador.databinding.ActivityCalendarBinding
import com.example.projintegrador.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.SimpleFormatter

class CalendarView : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityCalendarBinding

    val listaDiasSemanas = listOf("Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado")
    private val calendar = Calendar.getInstance() //criando o calendario
    private val formatter = SimpleDateFormat("MMM. dd, yyyy", Locale.US)


    private val database: FirebaseDatabase = Firebase.database
    private val dateFormatter = SimpleDateFormat("EEEE", Locale("pt", "BR"))
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())






    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)




        binding.btnSelecionarHora.setOnClickListener{
            val cal = calendar
            val timeSetListener = TimePickerDialog.OnTimeSetListener{timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.viewTime.text =  SimpleDateFormat("HH:mm").format(cal.time)
                salvarDataEHoraNoFirebase()

            }

            TimePickerDialog(this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()


        }


        binding.btnSelecionarHoraSaida.setOnClickListener {
            val cal = calendar
            val timeSetListener = TimePickerDialog.OnTimeSetListener{timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.viewTime2.text =  SimpleDateFormat("HH:mm").format(cal.time)
                salvarHoraSaidaNoFirebase()

            }

            TimePickerDialog(this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()


        }


        val adapterItems = ArrayAdapter<String>(this, R.layout.dropdown_menu_dias, listaDiasSemanas)
        binding.diaSemana.setAdapter(adapterItems)

        // Adiciona um ouvinte de eventos para o dropdown menu
        binding.diaSemana.setOnItemClickListener { parent, _, position, _ ->
            // Obtém o item selecionado no dropdown menu
            val selectedItem = parent.adapter.getItem(position).toString()
            val formato = SimpleDateFormat("EEEE", Locale("pt", "BR"))
            calendar.time = formato.parse(selectedItem)!!
            binding.viewDate.text = selectedItem
        }


        binding.imageButton.setOnClickListener{
            val navToMain = Intent(this, MainActivity::class.java)
            startActivity(navToMain)
        }

    }



    private fun salvarDataEHoraNoFirebase() {
        val userId = getUserId() // Obtém o ID do usuário atual

        val diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        val dataSelecionada = dateFormatter.format(calendar.time)
        val horaSelecionada = timeFormatter.format(calendar.time)

        val reference = database.reference.child("datas").child(userId).child(listaDiasSemanas[diaDaSemana - 1]) // Adiciona o ID do usuário e o dia da semana ao caminho do nó
        val novoHorario = reference.push()
        novoHorario.child("horaEntrada").setValue(horaSelecionada)
            .addOnSuccessListener {
                Toast.makeText(this, "Hora de entrada salva no Firebase com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao salvar hora de entrada no Firebase: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        // Adiciona a hora de saída no mesmo nó e chave que a hora de entrada
        novoHorario.child("horaSaida").setValue("") // Inicialmente, a hora de saída será vazia
    }

    private fun salvarHoraSaidaNoFirebase() {
        val userId = getUserId() // Obtém o ID do usuário atual

        val diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        val dataSelecionada = dateFormatter.format(calendar.time)
        val horaSelecionada = timeFormatter.format(calendar.time)

        val reference = database.reference.child("datas").child(userId).child(listaDiasSemanas[diaDaSemana - 1]) // Adiciona o ID do usuário e o dia da semana ao caminho do nó
        val ultimoHorario = reference.limitToLast(1) // Obtém o último horário adicionado
        ultimoHorario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        // Atualiza a hora de saída para o último horário adicionado
                        childSnapshot.ref.child("horaSaida").setValue(horaSelecionada)
                            .addOnSuccessListener {
                                Toast.makeText(this@CalendarView, "Hora de saída salva no Firebase com sucesso", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@CalendarView, "Falha ao salvar hora de entrada no Firebase: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Trate o erro, se necessário
                Log.e("Firebase", "Erro ao acessar o Firebase: ${error.message}")
            }
        })
    }



    private fun getUserId(): String {
        // Obtém o usuário atualmente autenticado
        val user = FirebaseAuth.getInstance().currentUser
        // Verifica se o usuário está autenticado e se o e-mail está disponível
        val userEmail = user?.email ?: throw IllegalStateException("Usuário não autenticado ou e-mail indisponível")
        // Substitui caracteres inválidos do e-mail por underscore ("_")
        return userEmail.replace(".", "_")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        displayFormattedDate(calendar.timeInMillis)
    }


    private fun displayFormattedDate(timestamp: Long){
        binding.viewDate.text = formatter.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }

    private fun limparTextViews() {
        binding.viewDate.text = ""
        binding.viewTime.text = ""
    }


}


