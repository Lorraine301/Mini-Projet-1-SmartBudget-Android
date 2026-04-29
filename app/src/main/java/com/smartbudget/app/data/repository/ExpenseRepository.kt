package com.smartbudget.app.data.repository

import com.smartbudget.app.data.local.dao.CategoryTotal
import com.smartbudget.app.data.local.dao.ExpenseDao
import com.smartbudget.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(private val expenseDao: ExpenseDao) {

    fun getExpensesByMonth(yearMonth: String): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesByMonth(yearMonth)

    fun getExpensesFiltered(yearMonth: String, categoryId: Long?, sortBy: String): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesFiltered(yearMonth, categoryId, sortBy)

    fun getTotalByMonth(yearMonth: String): Flow<Double?> =
        expenseDao.getTotalByMonth(yearMonth)

    fun getTotalByCategoryForMonth(yearMonth: String): Flow<List<CategoryTotal>> =
        expenseDao.getTotalByCategoryForMonth(yearMonth)

    fun getRecurringExpenses(): Flow<List<ExpenseEntity>> =
        expenseDao.getRecurringExpenses()

    suspend fun addExpense(expense: ExpenseEntity): Long =
        expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntity) =
        expenseDao.updateExpense(expense.copy(updatedAt = LocalDateTime.now()))

    suspend fun deleteExpense(expense: ExpenseEntity) =
        expenseDao.deleteExpense(expense)

    suspend fun getExpensesByMonthForExport(yearMonth: String): List<ExpenseEntity> =
        expenseDao.getExpensesByMonthForExport(yearMonth)
}