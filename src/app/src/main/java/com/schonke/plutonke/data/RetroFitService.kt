package com.schonke.plutonke.data
import com.schonke.plutonke.Expense
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetroFitService {
    @GET("expenses")
    suspend fun getAllExpenses(): Response<List<Expense>>
}

object RetroFitServiceFactory {
    fun makeRetroFitService(): RetroFitService {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.161:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetroFitService::class.java)
    }
}