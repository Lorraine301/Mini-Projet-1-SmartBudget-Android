package com.smartbudget.app.presentation.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.presentation.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Expenses : Screen("expenses", "Dépenses", Icons.Default.List)
    object Stats : Screen("stats", "Statistiques", Icons.Default.BarChart)
    object Settings : Screen("settings", "Paramètres", Icons.Default.Settings)
    object AddExpense : Screen("add_expense", "Ajouter", Icons.Default.List)
    object EditExpense : Screen("edit_expense/{expenseId}", "Modifier", Icons.Default.List)
}

val bottomNavItems = listOf(Screen.Expenses, Screen.Stats, Screen.Settings)

@Composable
fun SmartBudgetNavGraph() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    var expenseToEdit by remember { mutableStateOf<ExpenseEntity?>(null) }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Expenses.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Screen.Expenses.route) {
                ExpenseListScreen(
                    onAddExpense = { navController.navigate(Screen.AddExpense.route) },
                    onEditExpense = { expense ->
                        expenseToEdit = expense
                        navController.navigate("edit_expense/${expense.id}")
                    }
                )
            }
            composable(Screen.AddExpense.route) {
                AddEditExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable("edit_expense/{expenseId}") {
                AddEditExpenseScreen(
                    expenseToEdit = expenseToEdit,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}