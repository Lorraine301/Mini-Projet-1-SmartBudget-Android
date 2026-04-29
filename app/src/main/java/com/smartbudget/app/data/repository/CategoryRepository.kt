package com.smartbudget.app.data.repository

import com.smartbudget.app.data.local.dao.CategoryDao
import com.smartbudget.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    fun getActiveCategories(): Flow<List<CategoryEntity>> = categoryDao.getActiveCategories()

    suspend fun addCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) {
        val count = categoryDao.countExpensesForCategory(category.id)
        if (count > 0) {
            throw IllegalStateException("Impossible de supprimer : des dépenses existent pour cette catégorie.")
        }
        categoryDao.deleteCategory(category)
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? =
        categoryDao.getCategoryById(id)
}