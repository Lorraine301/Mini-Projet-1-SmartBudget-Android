package com.smartbudget.app.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbudget.app.data.local.entity.CategoryEntity
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.data.repository.CategoryRepository
import com.smartbudget.app.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SettingsUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val currency: String = "MAD",
    val exportMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // ── Dark Mode ─────────────────────────────────────────────────────────
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }
    }

    fun toggleCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category.copy(isActive = !category.isActive))
        }
    }

    fun addCategory(name: String, icon: String, color: String) {
        viewModelScope.launch {
            try {
                categoryRepository.addCategory(
                    CategoryEntity(name = name.trim(), icon = icon, color = color)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Ce nom de catégorie existe déjà."
                )
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun exportCsv(yearMonth: String) {
        viewModelScope.launch {
            try {
                val expenses   = expenseRepository.getExpensesByMonthForExport(yearMonth)
                val categories = categoryRepository.getAllCategories().first()
                val catMap     = categories.associateBy { it.id }
                val dateFmt    = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                val csv = buildString {
                    appendLine("Date,Montant,Devise,Catégorie,Note,Méthode paiement,Récurrent")
                    expenses.forEach { e ->
                        val catName = catMap[e.categoryId]?.name ?: "Inconnu"
                        appendLine(
                            "${e.date.format(dateFmt)}," +
                                    "${e.amount}," +
                                    "${e.currency}," +
                                    "$catName," +
                                    "${e.note ?: ""}," +
                                    "${e.paymentMethod ?: ""}," +
                                    "${if (e.isRecurring) "Oui" else "Non"}"
                        )
                    }
                }

                val file = File(context.getExternalFilesDir(null), "smartbudget_$yearMonth.csv")
                file.writeText(csv)
                _uiState.value = _uiState.value.copy(
                    exportMessage = "Exporté : ${file.absolutePath}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erreur export : ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(exportMessage = null, errorMessage = null)
    }
}