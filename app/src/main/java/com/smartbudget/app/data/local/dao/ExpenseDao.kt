package com.smartbudget.app.data.local.dao

import androidx.room.*
import com.smartbudget.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("""
        SELECT * FROM expenses 
        WHERE strftime('%Y-%m', date) = :yearMonth 
        ORDER BY date DESC
    """)
    fun getExpensesByMonth(yearMonth: String): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT * FROM expenses 
        WHERE strftime('%Y-%m', date) = :yearMonth 
        AND (:categoryId IS NULL OR categoryId = :categoryId)
        ORDER BY 
            CASE WHEN :sortBy = 'amount' THEN amount END DESC,
            CASE WHEN :sortBy = 'date' THEN date END DESC
    """)
    fun getExpensesFiltered(
        yearMonth: String,
        categoryId: Long?,
        sortBy: String = "date"
    ): Flow<List<ExpenseEntity>>

    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE strftime('%Y-%m', date) = :yearMonth
    """)
    fun getTotalByMonth(yearMonth: String): Flow<Double?>

    @Query("""
        SELECT categoryId, SUM(amount) as total 
        FROM expenses 
        WHERE strftime('%Y-%m', date) = :yearMonth 
        GROUP BY categoryId 
        ORDER BY total DESC
    """)
    fun getTotalByCategoryForMonth(yearMonth: String): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE isRecurring = 1")
    fun getRecurringExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("""
        SELECT * FROM expenses 
        WHERE strftime('%Y-%m', date) = :yearMonth 
        ORDER BY date ASC
    """)
    suspend fun getExpensesByMonthForExport(yearMonth: String): List<ExpenseEntity>
}

data class CategoryTotal(
    val categoryId: Long,
    val total: Double
)