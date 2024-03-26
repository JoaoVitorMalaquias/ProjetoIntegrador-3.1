package com.example.projetointegrador3

import android.app.Application

class GdcApplication : Application () {

    companion object {
        lateinit var instance: GdcApplication
    }
//pegando a instancia da aplicacao para a base de dados
    //vamos setar ele no manisfest (pasta)
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}