package com.smartbudget.app.data.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val isActive: Boolean = true
)