package com.schonke.plutonke.viewModels

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.types.ExpenseBackend
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val DATE_FORMAT = "dd/MM/yyyy"

@SuppressLint("SimpleDateFormat")
fun convertUnixTimestampToDate(unixTimestamp: Long): String {
    val date = Date(unixTimestamp * 1000)
    val format = SimpleDateFormat(DATE_FORMAT)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun convertDateToUnixTimestamp(dateString: String): Long {
    val format = SimpleDateFormat(DATE_FORMAT)
    val date = format.parse(dateString) ?: throw IOException("error converting date")
    return date.time / 1000
}

fun changeExpenseToBackendExpense(expense: Expense): ExpenseBackend {
    return ExpenseBackend(
        expense.id,
        expense.name,
        convertDateToUnixTimestamp(expense.date),
        expense.price,
        expense.categoryID
    )
}

fun changeBackendExpenseToExpense(expenseBackend: ExpenseBackend): Expense {
    return Expense(
        expenseBackend.id,
        expenseBackend.name,
        convertUnixTimestampToDate(expenseBackend.date),
        expenseBackend.price,
        expenseBackend.categoryID
    )
}

inline fun <reified T> fromJsonGeneric(json: String): T {
    val type = object : TypeToken<T>() {}.type
    return Gson().fromJson(json, type)
}
