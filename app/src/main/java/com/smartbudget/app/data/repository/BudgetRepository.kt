package com.smartbudget.app.data.repository

import com.smartbudget.app.data.local.dao.MonthlyBudgetDao
import com.smartbudget.app.data.local.entity.MonthlyBudgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(private val budgetDao: MonthlyBudgetDao) {

    fun getBudgetsByMonth(month: String): Flow<List<MonthlyBudgetEntity>> =
        budgetDao.getBudgetsByMonth(month)

    suspend fun setBudget(budget: MonthlyBudgetEntity) =
        budgetDao.insertOrUpdateBudget(budget)

    suspend fun deleteBudget(budget: MonthlyBudgetEntity) =
        budgetDao.deleteBudget(budget)
}