package com.example.projintegrador

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
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
import com.google.firebase.database.FirebaseDatabase
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



    private fun salvarDataEHoraNoFirebase() {
        val dataSelecionada = dateFormatter.format(calendar.time)
        val horaSelecionada = timeFormatter.format(calendar.time)

        val reference = database.reference.child("datas")
        val novaData = reference.push()
        novaData.child("data").setValue(dataSelecionada)
        novaData.child("hora").setValue(horaSelecionada)
            .addOnSuccessListener {
                Toast.makeText(this, "Data e hora salvas no Firebase com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao salvar data e hora no Firebase: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSalvarNoBD.setOnClickListener {
            salvarDataEHoraNoFirebase()
            limparTextViews()
        }


        binding.btnSelecionarHora.setOnClickListener{
            val cal = calendar
            val timeSetListener = TimePickerDialog.OnTimeSetListener{timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.viewTime.text =  SimpleDateFormat("HH:mm").format(cal.time)
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


