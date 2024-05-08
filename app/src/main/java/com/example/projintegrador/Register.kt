package com.example.projintegrador

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class Register : AppCompatActivity() {

    private var binding: ActivityRegistrarPontoBinding? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var userId: String
    private var pontoEntradaRegistrado = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarPontoBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)
        database = FirebaseDatabase.getInstance()

        userId = getUserId()



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
                REQUEST_LOCATION_PERMISSION
            )
        }

        database = Firebase.database

        userId = FirebaseAuth.getInstance().currentUser?.email?.replace(".", "_") ?: ""

        verificarHorarioPermitido()

        binding?.btnResgistrarPonto?.setOnClickListener {

            verificarRegistroDeEntrada { registroExiste ->
                if (registroExiste) {
                    registrarPontoSaida()
                } else {
                    registrarPontoEntrada()
                }
            }
        }
    }

    private fun verificarRegistroDeEntrada(callback: (Boolean) -> Unit) {
        val diaDaSemana = getDiaDaSemanaAtual()
        val referencia = database.getReference("RegistrarPonto").child(userId).child(diaDaSemana)

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verifica se existem registros para o dia atual
                if (snapshot.exists()) {
                    // Verifica se há algum registro de entrada
                    val registroEntrada = snapshot.children.firstOrNull { it.child("tipo").getValue(String::class.java) == "entrada" }
                    callback(registroEntrada != null)
                } else {
                    callback(false) // Não há registros para o dia atual
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Trate o erro, se necessário
                callback(false) // Assume que não há registros em caso de erro
            }
        })
    }



    private fun registrarPontoEntrada() {
        verificarLocalizacao {
            val timestamp = Date().time
            val salvarHoraMinuto = SimpleDateFormat("HH:mm")
            val horaMinutoSalvo = salvarHoraMinuto.format(timestamp)
            val diaDaSemana = getDiaDaSemanaAtual()

            val referencia = database.getReference("RegistrarPonto").child(userId).child(diaDaSemana)

            val novoRegistroRef = referencia.push()
            novoRegistroRef.child("tipo").setValue("entrada")
            novoRegistroRef.child("ponto").setValue(horaMinutoSalvo)
            novoRegistroRef.child("timestamp").setValue(timestamp)

            binding?.txtResgistrarPontoEntrada?.text = "Ponto de entrada registrado: $horaMinutoSalvo"
            Toast.makeText(this, "Ponto de entrada registrado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarPontoSaida() {
        verificarLocalizacao {
            val timestamp = Date().time
            val salvarHoraMinuto = SimpleDateFormat("HH:mm")
            val horaMinutoSalvo = salvarHoraMinuto.format(timestamp)
            val diaDaSemana = getDiaDaSemanaAtual()

            val referencia = database.getReference("RegistrarPonto").child(userId).child(diaDaSemana)

            referencia.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val registroAtual = snapshot.children.lastOrNull { it.child("tipo").getValue(String::class.java) == "entrada" }
                        if (registroAtual != null) {
                            registroAtual.ref.child("tipo").setValue("saida")
                            registroAtual.ref.child("ponto").setValue(horaMinutoSalvo)
                            registroAtual.ref.child("timestampSaida").setValue(timestamp)


                            val horaEntrada = registroAtual.child("ponto").getValue(String::class.java)
                            if (horaEntrada != null) {
                                val horarioSaidaValido = verificarHorarioSaidaValido(horaEntrada, horaMinutoSalvo)
                                if (horarioSaidaValido) {
                                    binding?.txtResgistrarPontoSaida?.text = "Ponto de saída registrado: $horaMinutoSalvo"
                                    Toast.makeText(this@Register, "Ponto de saída registrado com sucesso", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@Register, "Erro: O ponto de saída deve ser depois do ponto de entrada", Toast.LENGTH_SHORT).show()
                                    voltarParaMain()
                                }
                            }
                        } else {
                            // Se não houver registro de entrada para o dia atual, exiba uma mensagem de erro
                            Toast.makeText(this@Register, "Erro: Registre o ponto de entrada primeiro", Toast.LENGTH_SHORT).show()
                            voltarParaMain()
                        }
                    } else {
                        // Se não houver nenhum registro para o dia atual, crie um novo registro de saída
                        val novoRegistroRef = referencia.push()
                        novoRegistroRef.child("tipo").setValue("saida")
                        novoRegistroRef.child("ponto").setValue(horaMinutoSalvo)
                        novoRegistroRef.child("timestamp").setValue(timestamp)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Trate o erro, se necessário
                }
            })
        }
    }


    private fun getUserId(): String {
        // Obtém o usuário atualmente autenticado
        val user = FirebaseAuth.getInstance().currentUser
        // Verifica se o usuário está autenticado e se o e-mail está disponível
        val userEmail = user?.email ?: throw IllegalStateException("Usuário não autenticado ou e-mail indisponível")
        // Substitui caracteres inválidos do e-mail por underscore ("_")
        return userEmail.replace(".", "_")
    }


    private fun verificarHorarioSaidaValido(horaEntrada: String, horaSaida: String): Boolean {
        val horaEntradaParts = horaEntrada.split(":")
        val horaSaidaParts = horaSaida.split(":")

        val horaEntradaInt = horaEntradaParts[0].toInt()
        val minutoEntradaInt = horaEntradaParts[1].toInt()
        val horaSaidaInt = horaSaidaParts[0].toInt()
        val minutoSaidaInt = horaSaidaParts[1].toInt()

        if (horaSaidaInt < horaEntradaInt || (horaSaidaInt == horaEntradaInt && minutoSaidaInt <= minutoEntradaInt)) {
            return false
        }
        return true
    }


    private fun voltarParaMain() {
        val navigateToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(navigateToMainActivity)
    }


    private fun verificarHorarioPermitido() {
        val diaDaSemanaAtual = getDiaDaSemanaAtual()

        val referencia = database.getReference("datas").child(userId).child(diaDaSemanaAtual)

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val horaPermitida = childSnapshot.child("hora").getValue(String::class.java)
                        if (horaPermitida != null) {
                            val horaAtual = SimpleDateFormat("HH:mm").format(Date())
                            if (horaAtual == horaPermitida) {
                                // Hora atual é igual à hora permitida, permite o registro do ponto
                                binding?.btnResgistrarPonto?.isEnabled = true
                                return
                            }
                        }
                    }
                    // Nenhuma hora permitida encontrada para o dia selecionado
                    binding?.btnResgistrarPonto?.isEnabled = false
                    Toast.makeText(this@Register, "Erro: Nenhuma hora permitida encontrada para o dia selecionado", Toast.LENGTH_SHORT).show()
                    voltarParaMain()
                } else {
                    // Dia da semana atual não encontrado no banco de dados
                    binding?.btnResgistrarPonto?.isEnabled = false
                    Toast.makeText(this@Register, "Erro: Dia da semana não encontrado no banco de dados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Trate o erro, se necessário
                Log.e("Firebase", "Erro ao acessar o Firebase: ${error.message}")
            }
        })
    }


    @SuppressLint("MissingPermission")
    private fun verificarLocalizacao(callback: () -> Unit) {
        // Define as preferências para a solicitação de atualizações de localização.
        val locationRequest = LocationRequest.create()

        // Está definido como alta precisão, ou seja, vai pegar a localização mais próxima possível.
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest) // Adiciona a configuração de localização no construtor.

        // Este cliente é usado para realizar a verificação das configurações de localização.
        val client: SettingsClient = LocationServices.getSettingsClient(this)

        // Perguntar ao usuário se deseja compartilhar localização.
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        // Se ele permitir, acontece isso.
        task.addOnSuccessListener {
            // As configurações de localização estão OK, inicie a obtenção da localização atual.
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    // Verifique se a localização atual está dentro da localização desejada.
                    if (estaNaLocalizacaoDesejada(location)) {
                        // O usuário está na localização desejada, chame a função de callback.
                        callback()
                    } else {
                        // O usuário não está na localização desejada.
                        Toast.makeText(this, "Você precisa estar na localização desejada para bater o ponto", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    // Não foi possível obter a localização atual.
                    Toast.makeText(this, "Não foi possível obter a localização atual", Toast.LENGTH_SHORT).show()
                    voltarParaMain()
                }
        }

        task.addOnFailureListener {
            // As configurações de localização não estão OK, solicite ao usuário para ativá-las.
            Toast.makeText(this, "Ative as configurações de localização para bater o ponto", Toast.LENGTH_SHORT).show()
        }
    }


    private fun estaNaLocalizacaoDesejada(location: Location?): Boolean {
        // Coordenadas da localização desejada (exemplo)
        val latitudeDesejada = -22.83450045123378
        //val longitudeDesejada = -190.05276164551796
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


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }
}
