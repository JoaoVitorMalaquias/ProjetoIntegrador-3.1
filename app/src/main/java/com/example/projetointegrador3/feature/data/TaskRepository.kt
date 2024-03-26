package com.example.projetointegrador3.feature.data

import androidx.lifecycle.LiveData
import com.example.projetointegrador3.feature.data.entity.taskDto
import com.example.projetointegrador3.feature.data.local.TaskDao

class TaskRepository private constructor(
    //passamos o localDataSource, que é o DAO
    private val  localDataSource: TaskDao
) {

    //adicionamos uma nova task
    suspend fun addTask(taskDto: taskDto){
        localDataSource.insert(taskDto)
    }

    //tras uma liveData com um lista de todas as tasks que eu tenho no BD
    fun getAllTasks(): LiveData<List<taskDto>> = localDataSource.getAllTasks()



    //Esse tipo de objeto é inicializado quando a classe que carrega ele é carregada/resolvida.
    //Isso lembra muito o comportamento da incialização static do Java.
    //conseguir staticamente criar um novo repositorio
    companion object{

        //criar um repositorio de retorno
        fun create(localDataSource: TaskDao): TaskRepository{
            return TaskRepository(localDataSource)
        }
    }

}