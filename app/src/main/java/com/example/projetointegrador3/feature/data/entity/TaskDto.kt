package com.example.projetointegrador3.feature.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


//Criamos um Data class
@Entity(tableName = "task_table") //criamos um table
data class taskDto (
    @PrimaryKey(autoGenerate = true) val id: Int = 0, //primary key, que vai se auto incrementar no bd
    val name: String,
    val state: String = Status.TODO.name //default argument
    //ele sabe que para criar uma nova tarefa, ele precisa apenas do nome.
)



enum class Status{
    TODO,
    PROGRESS,
    DONE
}
