package com.smartbudget.app.presentation.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.presentation.ui.screens.*
import com.smartbudget.app.presentation.viewmodel.ExpenseViewModel
import com.smartbudget.app.presentation.viewmodel.SettingsViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Welcome    : Screen("welcome",                 "Accueil",      Icons.Default.List)
    object Expenses   : Screen("expenses",                "Dépenses",     Icons.Default.List)
    object Stats      : Screen("stats",                   "Statistiques", Icons.Default.BarChart)
    object Settings   : Screen("settings",                "Paramètres",   Icons.Default.Settings)
    object AddExpense : Screen("add_expense",              "Ajouter",      Icons.Default.List)
    object EditExpense: Screen("edit_expense/{expenseId}", "Modifier",     Icons.Default.List)
    object Detail     : Screen("detail/{expenseId}",      "Détail",       Icons.Default.List)
}

val bottomNavItems = listOf(Screen.Expenses, Screen.Stats, Screen.Settings)

@Composable
fun SmartBudgetNavGraph(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navController    = rememberNavController()
    val navBackStack     by navController.currentBackStackEntryAsState()
    val currentRoute     = navBackStack?.destination?.route
    val showBottomBar    = currentRoute in bottomNavItems.map { it.route }

    // Stockage des dépenses sélectionnées
    var expenseToEdit    by remember { mutableStateOf<ExpenseEntity?>(null) }
    var expenseForDetail by remember { mutableStateOf<ExpenseEntity?>(null) }

    val settingsUiState  by settingsViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon     = { Icon(screen.icon, contentDescription = screen.label) },
                            label    = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick  = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Welcome.route,
            modifier         = Modifier.padding(padding)
        ) {

            // ── Welcome ───────────────────────────────────────────
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Expenses.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Liste des dépenses ────────────────────────────────
            composable(Screen.Expenses.route) {
                ExpenseListScreen(
                    onAddExpense  = {
                        navController.navigate(Screen.AddExpense.route)
                    },
                    onEditExpense = { expense: ExpenseEntity ->
                        expenseToEdit = expense
                        navController.navigate("edit_expense/${expense.id}")
                    },
                    onViewDetail  = { expense: ExpenseEntity ->
                        expenseForDetail = expense
                        navController.navigate("detail/${expense.id}")
                    }
                )
            }

            // ── Ajouter dépense ───────────────────────────────────
            composable(Screen.AddExpense.route) {
                AddEditExpenseScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ── Modifier dépense ──────────────────────────────────
            composable("edit_expense/{expenseId}") {
                AddEditExpenseScreen(
                    expenseToEdit = expenseToEdit,
                    onBack        = { navController.popBackStack() }
                )
            }

            // ── Détail dépense ────────────────────────────────────
            composable("detail/{expenseId}") {
                val currentExpense: ExpenseEntity? = expenseForDetail
                if (currentExpense != null) {
                    val category = settingsUiState.categories
                        .find { cat -> cat.id == currentExpense.categoryId }

                    val expenseViewModel: ExpenseViewModel = hiltViewModel()
                    val expenseUiState by expenseViewModel.uiState.collectAsState()

                    val isTopExpense: Boolean = expenseUiState.expenses
                        .maxByOrNull { exp -> exp.amount }
                        ?.id == currentExpense.id

                    ExpenseDetailScreen(
                        expense      = currentExpense,
                        category     = category,
                        isTopExpense = isTopExpense,
                        onBack       = { navController.popBackStack() },
                        onEdit       = {
                            expenseToEdit = currentExpense
                            navController.navigate("edit_expense/${currentExpense.id}")
                        }
                    )
                }
            }

            // ── Statistiques ──────────────────────────────────────
            composable(Screen.Stats.route) {
                StatsScreen()
            }

            // ── Paramètres ────────────────────────────────────────
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}