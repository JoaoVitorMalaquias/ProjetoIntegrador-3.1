package com.example.projetointegrador3.feature.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projetointegrador3.feature.data.entity.taskDto

//o Dao é onde você executa as querys para acessar a base de dados


@Dao
interface TaskDao {


    @Query("SELECT * FROM task_table ORDER BY name ASC")
    //essa função devolve uma lista de tasksDto
    fun getAllTasks(): LiveData<List<taskDto>>

    @Query("SELECT * FROM task_table WHERE id = :id")
    fun getTaskById(id: Long): LiveData<taskDto>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(taskDto: taskDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //insere dentro da fase de dados
    suspend fun insert (TaskDto: taskDto)

    @Query("DELETE FROM task_table")
    fun deleteAll()

    @Query("DELETE FROM task_table WHERE id = :id")
    fun deleteById(id: Long)
}