package com.schonke.plutonke.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense

class HomeScreenViewModel(private val sharedViewModel: SharedDataViewModel) : ViewModel() {
        val sharedExpenses: LiveData<List<Expense>?> get() = sharedViewModel.sharedExpenses
        val sharedCategories: LiveData<List<Category>?> get() = sharedViewModel.sharedCategories
        val dataUpdated: LiveData<Boolean> get() = sharedViewModel.dataUpdated

}