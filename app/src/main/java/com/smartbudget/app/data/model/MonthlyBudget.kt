package com.smartbudget.app.data.model

data class MonthlyBudget(
    val id: Long = 0,
    val month: String,
    val categoryId: Long,
    val limitAmount: Double
)