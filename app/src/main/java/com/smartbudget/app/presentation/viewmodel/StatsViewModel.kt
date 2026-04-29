package com.smartbudget.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartbudget.app.data.local.dao.CategoryTotal
import com.smartbudget.app.data.local.entity.CategoryEntity
import com.smartbudget.app.data.local.entity.MonthlyBudgetEntity
import com.smartbudget.app.data.repository.BudgetRepository
import com.smartbudget.app.data.repository.CategoryRepository
import com.smartbudget.app.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CategoryStat(
    val category: CategoryEntity,
    val total: Double,
    val percentage: Double,
    val budget: MonthlyBudgetEntity? = null
)

data class StatsUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalMonth: Double = 0.0,
    val totalPreviousMonth: Double = 0.0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

    init {
        loadStats()
    }

    private fun loadStats() {
        val month = _uiState.value.selectedMonth
        val yearMonth = month.format(formatter)
        val prevYearMonth = month.minusMonths(1).format(formatter)

        viewModelScope.launch {
            combine(
                expenseRepository.getTotalByMonth(yearMonth),
                expenseRepository.getTotalByMonth(prevYearMonth),
                expenseRepository.getTotalByCategoryForMonth(yearMonth),
                categoryRepository.getAllCategories(),
                budgetRepository.getBudgetsByMonth(yearMonth)
            ) { total, prevTotal, categoryTotals, categories, budgets ->

                val grandTotal = total ?: 0.0
                val catMap = categories.associateBy { it.id }
                val budgetMap = budgets.associateBy { it.categoryId }

                val stats = categoryTotals.mapNotNull { ct ->
                    val cat = catMap[ct.categoryId] ?: return@mapNotNull null
                    CategoryStat(
                        category = cat,
                        total = ct.total,
                        percentage = if (grandTotal > 0) (ct.total / grandTotal) * 100 else 0.0,
                        budget = budgetMap[ct.categoryId]
                    )
                }.sortedByDescending { it.total }

                StatsUiState(
                    selectedMonth = month,
                    totalMonth = grandTotal,
                    totalPreviousMonth = prevTotal ?: 0.0,
                    categoryStats = stats,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun goToPreviousMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth.minusMonths(1),
            isLoading = true
        )
        loadStats()
    }

    fun goToNextMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth.plusMonths(1),
            isLoading = true
        )
        loadStats()
    }
}