package com.smartbudget.app.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbudget.app.data.local.entity.CategoryEntity
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.data.repository.CategoryRepository
import com.smartbudget.app.data.repository.ExpenseRepository
import com.smartbudget.app.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ExpenseUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val totalMonth: Double = 0.0,
    val selectedMonth: YearMonth = YearMonth.now(),
    val selectedCategoryId: Long? = null,
    val sortBy: String = "date",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

    // Budget global limite (4000 MAD)
    private val GLOBAL_BUDGET_LIMIT = 4000.0
    private var lastNotifiedTotal = 0.0

    init {
        loadData()
    }

    private fun loadData() {
        val yearMonth = _uiState.value.selectedMonth.format(formatter)

        viewModelScope.launch {
            combine(
                expenseRepository.getExpensesFiltered(
                    yearMonth,
                    _uiState.value.selectedCategoryId,
                    _uiState.value.sortBy
                ),
                expenseRepository.getTotalByMonth(yearMonth),
                categoryRepository.getActiveCategories()
            ) { expenses, total, categories ->
                _uiState.value.copy(
                    expenses = expenses,
                    totalMonth = total ?: 0.0,
                    categories = categories,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun goToPreviousMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth.minusMonths(1)
        )
        loadData()
    }

    fun goToNextMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth.plusMonths(1)
        )
        loadData()
    }

    fun setFilterCategory(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        loadData()
    }

    fun setSortBy(sortBy: String) {
        _uiState.value = _uiState.value.copy(sortBy = sortBy)
        loadData()
    }

    fun addExpense(
        amount: Double,
        categoryId: Long,
        date: LocalDate,
        note: String?,
        paymentMethod: String?,
        isRecurring: Boolean
    ) {
        viewModelScope.launch {
            try {
                val expense = ExpenseEntity(
                    amount = amount,
                    categoryId = categoryId,
                    date = date,
                    note = note?.ifBlank { null },
                    paymentMethod = paymentMethod,
                    isRecurring = isRecurring
                )
                expenseRepository.addExpense(expense)
                // Vérifier le budget global après chaque ajout
                checkGlobalBudgetAlert()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            try {
                expenseRepository.updateExpense(expense)
                checkGlobalBudgetAlert()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // ── Vérification budget global 4000 MAD ──────────────────────────────
    private fun checkGlobalBudgetAlert() {
        viewModelScope.launch {
            val yearMonth = _uiState.value.selectedMonth.format(formatter)

            // first() pour récupérer la valeur actuelle une seule fois
            val total = expenseRepository.getTotalByMonth(yearMonth).first() ?: 0.0

            Log.d("ExpenseViewModel", "Total actuel : $total MAD / limite : $GLOBAL_BUDGET_LIMIT MAD")

            if (total > GLOBAL_BUDGET_LIMIT && lastNotifiedTotal <= GLOBAL_BUDGET_LIMIT) {
                Log.d("ExpenseViewModel", "Envoi notification dépassement budget !")
                NotificationHelper.sendGlobalBudgetAlert(context, total, GLOBAL_BUDGET_LIMIT)
            }
            lastNotifiedTotal = total
        }
    }

    // ── Retourne la dépense la plus chère du mois courant ─────────────────
    fun getTopExpenseId(): Long? {
        return _uiState.value.expenses.maxByOrNull { it.amount }?.id
    }
}