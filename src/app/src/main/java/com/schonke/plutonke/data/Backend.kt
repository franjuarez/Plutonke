package com.schonke.plutonke.data
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

const val SERVER_URL = "http://10.0.2.2:8080/"  //Para emulador
//const val SERVER_URL = "http://192.168.0.161:8080/" //Para celular

interface Backend {
    @GET("expenses")
    suspend fun getAllExpenses(): Response<List<Expense>>

    @POST("expenses")
    suspend fun addExpense(@Body expense: Expense): Response<Expense>

    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") expenseId: String, @Body expense: Expense): Response<Expense>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") expenseId: String): Response<Void>

    @GET("categories")
    suspend fun getAllCategories(): Response<List<Category>>

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") categoryId: String, @Body category: Category): Response<Category>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") categoryId: String): Response<Void>
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