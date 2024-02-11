package com.schonke.plutonke.data
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

const val SERVER_URL = "http://10.0.2.2:8080/"  //Para emulador
//const val SERVER_URL = "http://192.168.0.161:8080/" //Para celular

interface Backend {
    @GET("expenses")
    suspend fun getAllExpenses(): Response<List<Expense>>

    @POST("expenses")
    suspend fun addExpense(expense: Expense): Response<Expense>

    @GET("categories")
    suspend fun getAllCategories(): Response<List<Category>>
}

object BackendFactory {

    private val backend = Retrofit.Builder()
        .baseUrl(SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun getInstance(): Backend {
        return backend.create(Backend::class.java)
    }
}