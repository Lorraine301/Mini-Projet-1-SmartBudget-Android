package com.smartbudget.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartbudget.app.data.local.dao.CategoryDao
import com.smartbudget.app.data.local.dao.ExpenseDao
import com.smartbudget.app.data.local.dao.MonthlyBudgetDao
import com.smartbudget.app.data.local.entity.CategoryEntity
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.data.local.entity.MonthlyBudgetEntity
import com.smartbudget.app.util.DateConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@Database(
    entities = [CategoryEntity::class, ExpenseEntity::class, MonthlyBudgetEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class SmartBudgetDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun monthlyBudgetDao(): MonthlyBudgetDao

    companion object {
        @Volatile private var INSTANCE: SmartBudgetDatabase? = null

        fun getDatabase(context: Context): SmartBudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SmartBudgetDatabase::class.java,
                    "smartbudget.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDatabase(database)
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
        }

        private suspend fun seedDatabase(db: SmartBudgetDatabase) {
            val categoryDao = db.categoryDao()
            val expenseDao = db.expenseDao()

            // Seed catégories
            val categories = listOf(
                CategoryEntity(name = "Alimentation", icon = "🍔", color = "#FF6B6B"),
                CategoryEntity(name = "Transport", icon = "🚌", color = "#4ECDC4"),
                CategoryEntity(name = "Logement", icon = "🏠", color = "#45B7D1"),
                CategoryEntity(name = "Santé", icon = "💊", color = "#96CEB4"),
                CategoryEntity(name = "Loisirs", icon = "🎮", color = "#FFEAA7"),
                CategoryEntity(name = "Études", icon = "📚", color = "#DDA0DD"),
                CategoryEntity(name = "Autre", icon = "📦", color = "#B0B0B0")
            )
            val ids = categories.map { categoryDao.insertCategory(it) }

            // Seed dépenses — mois courant (avril 2026)
            val now = LocalDate.now()
            val currentMonth = now.withDayOfMonth(1)
            val lastMonth = currentMonth.minusMonths(1)

            val expenses = mutableListOf<ExpenseEntity>()

            // Avril 2026
            expenses.addAll(listOf(
                ExpenseEntity(amount = 45.0, date = currentMonth.plusDays(0), categoryId = ids[0], note = "Déjeuner université"),
                ExpenseEntity(amount = 120.0, date = currentMonth.plusDays(1), categoryId = ids[1], note = "Abonnement bus"),
                ExpenseEntity(amount = 200.0, date = currentMonth.plusDays(1), categoryId = ids[5], note = "Livres cours"),
                ExpenseEntity(amount = 35.0, date = currentMonth.plusDays(2), categoryId = ids[0], note = "Courses supermarché"),
                ExpenseEntity(amount = 80.0, date = currentMonth.plusDays(3), categoryId = ids[4], note = "Cinéma + resto"),
                ExpenseEntity(amount = 15.0, date = currentMonth.plusDays(4), categoryId = ids[0], note = "Café + sandwich"),
                ExpenseEntity(amount = 55.0, date = currentMonth.plusDays(5), categoryId = ids[3], note = "Pharmacie"),
                ExpenseEntity(amount = 2500.0, date = currentMonth.plusDays(5), categoryId = ids[2], note = "Loyer avril", isRecurring = true),
                ExpenseEntity(amount = 25.0, date = currentMonth.plusDays(6), categoryId = ids[1], note = "Taxi"),
                ExpenseEntity(amount = 60.0, date = currentMonth.plusDays(7), categoryId = ids[0], note = "Courses semaine"),
                ExpenseEntity(amount = 150.0, date = currentMonth.plusDays(8), categoryId = ids[5], note = "Impression rapport"),
                ExpenseEntity(amount = 40.0, date = currentMonth.plusDays(9), categoryId = ids[4], note = "Jeu mobile"),
                ExpenseEntity(amount = 30.0, date = currentMonth.plusDays(10), categoryId = ids[0], note = "Restaurant"),
                ExpenseEntity(amount = 20.0, date = currentMonth.plusDays(11), categoryId = ids[1], note = "Essence moto"),
                ExpenseEntity(amount = 75.0, date = currentMonth.plusDays(12), categoryId = ids[6], note = "Divers")
            ))

            // Mars 2026
            expenses.addAll(listOf(
                ExpenseEntity(amount = 2500.0, date = lastMonth.plusDays(0), categoryId = ids[2], note = "Loyer mars", isRecurring = true),
                ExpenseEntity(amount = 120.0, date = lastMonth.plusDays(1), categoryId = ids[1], note = "Abonnement bus"),
                ExpenseEntity(amount = 55.0, date = lastMonth.plusDays(2), categoryId = ids[0], note = "Courses"),
                ExpenseEntity(amount = 90.0, date = lastMonth.plusDays(3), categoryId = ids[5], note = "Papeterie"),
                ExpenseEntity(amount = 40.0, date = lastMonth.plusDays(5), categoryId = ids[4], note = "Sortie ciné"),
                ExpenseEntity(amount = 25.0, date = lastMonth.plusDays(6), categoryId = ids[0], note = "Déjeuner"),
                ExpenseEntity(amount = 110.0, date = lastMonth.plusDays(7), categoryId = ids[3], note = "Consultation médecin"),
                ExpenseEntity(amount = 35.0, date = lastMonth.plusDays(9), categoryId = ids[0], note = "Épicerie"),
                ExpenseEntity(amount = 60.0, date = lastMonth.plusDays(11), categoryId = ids[1], note = "Train"),
                ExpenseEntity(amount = 180.0, date = lastMonth.plusDays(12), categoryId = ids[5], note = "Cours en ligne"),
                ExpenseEntity(amount = 45.0, date = lastMonth.plusDays(14), categoryId = ids[4], note = "Bowling"),
                ExpenseEntity(amount = 30.0, date = lastMonth.plusDays(15), categoryId = ids[0], note = "Café étude"),
                ExpenseEntity(amount = 50.0, date = lastMonth.plusDays(17), categoryId = ids[6], note = "Cadeau"),
                ExpenseEntity(amount = 20.0, date = lastMonth.plusDays(19), categoryId = ids[1], note = "Parking"),
                ExpenseEntity(amount = 70.0, date = lastMonth.plusDays(21), categoryId = ids[3], note = "Médicaments")
            ))

            expenses.forEach { expenseDao.insertExpense(it) }
        }
    }
}