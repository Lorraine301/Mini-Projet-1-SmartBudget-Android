package com.smartbudget.app.di

import android.content.Context
import androidx.room.Room
import com.smartbudget.app.data.local.SmartBudgetDatabase
import com.smartbudget.app.data.local.dao.CategoryDao
import com.smartbudget.app.data.local.dao.ExpenseDao
import com.smartbudget.app.data.local.dao.MonthlyBudgetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartBudgetDatabase =
        SmartBudgetDatabase.getDatabase(context)

    @Provides fun provideCategoryDao(db: SmartBudgetDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideExpenseDao(db: SmartBudgetDatabase): ExpenseDao = db.expenseDao()
    @Provides fun provideMonthlyBudgetDao(db: SmartBudgetDatabase): MonthlyBudgetDao = db.monthlyBudgetDao()
}