package com.smartbudget.app.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val currency: String = "MAD",
    val date: LocalDate,
    val categoryId: Long,
    val note: String? = null,
    val paymentMethod: String? = null,
    val isRecurring: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)