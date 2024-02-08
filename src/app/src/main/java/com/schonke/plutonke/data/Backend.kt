package com.schonke.plutonke.data
import com.schonke.plutonke.types.Expense
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val SERVER_URL = "http://10.0.2.2:8080/"  //Para emulador
//const val SERVER_URL = "http://192.168.0.161:8080/" //Para celular

interface Backend {
    @GET("expenses")
    suspend fun getAllExpenses(): Response<List<Expense>>
}

object BackendFactory {

    private val backend = Retrofit.Builder()
        .baseUrl(SERVER_URL) //Para emulador
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun getInstance(): Backend {
        return backend.create(Backend::class.java)
    }
}