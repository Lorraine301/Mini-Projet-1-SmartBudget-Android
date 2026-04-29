package com.smartbudget.app.data.local.dao

import androidx.room.*
import com.smartbudget.app.data.local.entity.MonthlyBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyBudgetDao {
    @Query("SELECT * FROM monthly_budgets WHERE month = :month")
    fun getBudgetsByMonth(month: String): Flow<List<MonthlyBudgetEntity>>

    @Query("SELECT * FROM monthly_budgets WHERE month = :month AND categoryId = :categoryId")
    suspend fun getBudgetForCategory(month: String, categoryId: Long): MonthlyBudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: MonthlyBudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: MonthlyBudgetEntity)
}