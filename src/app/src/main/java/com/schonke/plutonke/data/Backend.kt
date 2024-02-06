package com.schonke.plutonke.data
import com.schonke.plutonke.Expense
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Backend {
    @GET("expenses")
    suspend fun getAllExpenses(): Response<List<Expense>>
}

object BackendFactory {
    fun makeBackend(): Backend {
        return Retrofit.Builder()
//            .baseUrl("http://192.168.0.161:8080/") //Para celular
            .baseUrl("http://10.0.2.2:8080/") //Para emulador
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Backend::class.java)
    }
}