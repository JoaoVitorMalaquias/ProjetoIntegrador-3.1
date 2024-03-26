package com.example.projetointegrador3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.projetointegrador3.databinding.ActivityMainBinding
import com.example.projetointegrador3.feature.data.entity.taskDto
import com.example.projetointegrador3.feature.presentation.TaskListViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(
            this,
            TaskListViewModel.TaskViewModelFactory(GdcApplication.instance) //criando um viewmodel especifico daquela instancia
        ).get(TaskListViewModel::class.java)

        viewModel.allTasks.observe(this,{
            Log.d("MytaskList", it.toString())
            Toast.makeText(this, it.size.toString(), Toast.LENGTH_SHORT).show()
        })

        viewModel.addTask(taskDto(name = "Teste"))


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


}