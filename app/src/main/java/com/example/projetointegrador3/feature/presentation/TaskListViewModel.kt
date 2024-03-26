package com.example.projetointegrador3.feature.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projetointegrador3.base.AppDataBase
import com.example.projetointegrador3.feature.data.TaskRepository
import com.example.projetointegrador3.feature.data.entity.taskDto
import kotlinx.coroutines.launch

class TaskListViewModel (application: Application): AndroidViewModel(application){


    //aqui estou falando que vou ter um repositorio e
    // um livedata com todas as minhas tasks
    private val repository: TaskRepository
    val allTasks: LiveData<List<taskDto>>


    init {
        val dao = AppDataBase.getDataBase(application).taskDao() //criando meu appdatabase, pegando meu taskdao passando ele para meu repositorio
        repository = TaskRepository.create(dao)  //criando um repositorio
        allTasks = repository.getAllTasks() //pegando as tasks já direto da database
    }


    //aqui eu acesso a base de dados, podendo adicionar algo la
    fun addTask(taskDto: taskDto){
        viewModelScope.launch{
            repository.addTask(taskDto)
        }
    }


    //com o novo androidx, as vezes não é necessario!
    class TaskViewModelFactory constructor(private val application: Application):
            ViewModelProvider.Factory{

                override fun <T: ViewModel> create(modelClass: Class<T>): T{
                    //passando o tipo da instancia q eu quero do meu viewmodel
                    return if (modelClass.isAssignableFrom(TaskListViewModel::class.java)){
                        TaskListViewModel(this.application) as T

                    }else{ //caso ele nao encontre, cai no else

                        throw IllegalArgumentException("ViewModel Not Found")

                    }
                }
            }


}