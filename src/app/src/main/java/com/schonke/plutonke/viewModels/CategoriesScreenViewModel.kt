package com.schonke.plutonke.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.types.Category

class CategoriesScreenViewModel(private val sharedViewModel: SharedDataViewModel) : ViewModel() {
    val sharedCategories: LiveData<List<Category>> get() = sharedViewModel.sharedCategories
    val dataUpdated: LiveData<Boolean> get() = sharedViewModel.dataUpdated
}
