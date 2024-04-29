package com.example.projintegrador

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.projintegrador.databinding.ActivityRegistrarPontoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Register : AppCompatActivity() {

    private var binding: ActivityRegistrarPontoBinding? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarPontoBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permissões de geolocalização se não estiverem concedidas
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Companion.REQUEST_LOCATION_PERMISSION
            )
        }



        database = Firebase.database

        binding?.btnResgistrarPontoEntrada?.setOnClickListener {

            verificarLocalizacao()
        }

        binding?.btnResgistrarPontoSaida?.setOnClickListener {
            registrarPontoSaida()
        }




    }

    private fun registrarPontoEntrada() {
        val timestamp = Date().time
        val salvarHoraMinuto = SimpleDateFormat("HH:mm")
        val HoraMinutoSalvo = salvarHoraMinuto.format(timestamp)
        val diaDaSemana = getDiaDaSemanaAtual()

        val referencia = database.getReference("RegistrarPonto")
        referencia.child("pontoEntrada").setValue(HoraMinutoSalvo)
            .addOnSuccessListener {
                // Salvar o dia da semana também
                referencia.child("diaDaSemana").setValue(diaDaSemana)
                binding?.txtResgistrarPontoEntrada?.text = "Ponto de entrada registrado: $HoraMinutoSalvo"
                Toast.makeText(this, "Ponto de entrada registrado com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao registrar ponto de entrada: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registrarPontoSaida() {
        val timestamp = Date().time
        val salvarHoraMinuto = SimpleDateFormat("HH:mm")
        val HoraMinutoSalvo = salvarHoraMinuto.format(timestamp)
        val diaDaSemana = getDiaDaSemanaAtual()

        val referencia = database.getReference("RegistrarPonto")

        // Verifica se o ponto de entrada foi registrado
        referencia.child("pontoEntrada").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Ponto de entrada registrado, agora registre o ponto de saída
                referencia.child("pontoSaida").setValue(HoraMinutoSalvo)
                    .addOnSuccessListener {
                        // Salvar o dia da semana também
                        referencia.child("diaDaSemana").setValue(diaDaSemana)
                        binding?.txtResgistrarPontoSaida?.text = "Ponto de saída registrado: $HoraMinutoSalvo"
                        Toast.makeText(this, "Ponto de saída registrado com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Falha ao registrar ponto de saída: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Ponto de entrada não registrado
                Toast.makeText(this, "Registre o ponto de entrada primeiro", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao verificar o ponto de entrada: ${it.message}", Toast.LENGTH_SHORT).show()
        }
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

    @SuppressLint("MissingPermission")
    private fun verificarLocalizacao() {
        //define as preferências para a solicitação de atualizações de localização.
        val locationRequest = LocationRequest.create()

        //está definido como alta precisao, ou seja, vai pegar a localizao mais proxima possivel
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest) //adiciona a configuracao de localizacao no construtor

        //Esse cliente é usado para realizar a verificação das configurações de localização.
        val client: SettingsClient = LocationServices.getSettingsClient(this)


        //perguntar ao usuario se deseja compartilhar localizacao
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        //se ele permitir acontece isso
        task.addOnSuccessListener {
            // As configurações de localização estão OK, inicie a obtenção da localização atual
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    // Verifique se a localização atual está dentro da localização desejada
                    if (estaNaLocalizacaoDesejada(location)) {
                        // O usuário está na localização desejada, permita que ele bata o ponto
                        registrarPontoEntrada()
                    } else {
                        // O usuário não está na localização desejada
                        Toast.makeText(this, "Você precisa estar na localização desejada para bater o ponto", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    // Não foi possível obter a localização atual
                    Toast.makeText(this, "Não foi possível obter a localização atual", Toast.LENGTH_SHORT).show()
                }
        }

        task.addOnFailureListener {
            // As configurações de localização não estão OK, solicite ao usuário para ativá-las
            Toast.makeText(this, "Ative as configurações de localização para bater o ponto", Toast.LENGTH_SHORT).show()
        }
    }


    private fun estaNaLocalizacaoDesejada(location: Location?): Boolean {
        // Coordenadas da localização desejada (exemplo)
        val latitudeDesejada = -22.83450045123378
        val longitudeDesejada = -47.05276164551796

        // Verifique se a localização atual não é nula
        if (location != null) {
            // Coordenadas da localização atual
            val latitudeAtual = location.latitude
            val longitudeAtual = location.longitude

            // Distância máxima permitida (em metros) para considerar que o usuário está na localização desejada
            val distanciaMaximaPermitida = 1000 // Altere conforme necessário

            // Calcule a distância entre as coordenadas da localização atual e as coordenadas da localização desejada
            val distancia = calcularDistancia(latitudeAtual, longitudeAtual, latitudeDesejada, longitudeDesejada)

            // Verifique se a distância está dentro da distância máxima permitida
            return distancia <= distanciaMaximaPermitida
        }

        return false
    }

    private fun calcularDistancia(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }
}
