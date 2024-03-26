package com.example.projetointegrador3.base


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projetointegrador3.feature.data.entity.taskDto
import com.example.projetointegrador3.feature.data.local.TaskDao



//esse @database está falando qual tipo de tabela nós vamos ter, a versão
//o "version" serve para toda vez que voce faz alguma alteração, você adiciona a vesão
//o exportSchema é para você exportar e manter um historico da sua base de dados
@Database(entities = [taskDto::class], version = 1, exportSchema = false)
//ele extende o RoomDataBase, para criar uma nova tabela
//criamos a data base, é uma classe abstrata também
abstract class AppDataBase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object{
        @Volatile //serve para que evite que a base
        //de dados seja alterada em diferente threads
        private var instance: AppDataBase? = null

        //acessa a base de dados
        fun getDataBase(context: android.content.Context): AppDataBase =
            instance ?: synchronized(this){
                instance ?: buildDataBase(context).also{
                    instance = it
                }
            }

        private fun buildDataBase(context: android.content.Context) =
            Room.databaseBuilder(context, AppDataBase::class.java, "task_table")
                .fallbackToDestructiveMigration()
                .build()


    }

}