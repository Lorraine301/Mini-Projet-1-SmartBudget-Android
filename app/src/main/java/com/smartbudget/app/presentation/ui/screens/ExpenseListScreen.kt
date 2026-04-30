package com.smartbudget.app.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.presentation.ui.components.CategoryChip
import com.smartbudget.app.presentation.ui.components.ExpenseItem
import com.smartbudget.app.presentation.ui.components.MonthNavigator
import com.smartbudget.app.presentation.viewmodel.ExpenseViewModel

@Composable
fun ExpenseListScreen(
    onAddExpense: () -> Unit,
    onEditExpense: (ExpenseEntity) -> Unit,
    onViewDetail: (ExpenseEntity) -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<ExpenseEntity?>(null) }

    val topExpenseId: Long? = uiState.expenses
        .maxByOrNull { it.amount }?.id

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpense,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Ajouter",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Navigation mois ─────────────────────────
            MonthNavigator(
                selectedMonth = uiState.selectedMonth,
                onPrevious = viewModel::goToPreviousMonth,
                onNext = viewModel::goToNextMonth,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // ── Carte total ─────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total du mois",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${String.format("%.2f", uiState.totalMonth)} MAD",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // ── Catégories ──────────────────────────────
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    CategoryChip(
                        category = null,
                        isSelected = uiState.selectedCategoryId == null,
                        onClick = { viewModel.setFilterCategory(null) }
                    )
                }
                items(uiState.categories) { cat ->
                    CategoryChip(
                        category = cat,
                        isSelected = uiState.selectedCategoryId == cat.id,
                        onClick = { viewModel.setFilterCategory(cat.id) }
                    )
                }
            }

            // ── Tri ─────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Trier :",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                FilterChip(
                    selected = uiState.sortBy == "date",
                    onClick = { viewModel.setSortBy("date") },
                    label = { Text("Date") }
                )
                FilterChip(
                    selected = uiState.sortBy == "amount",
                    onClick = { viewModel.setSortBy("amount") },
                    label = { Text("Montant") }
                )
            }

            // ── CONTENU PRINCIPAL (FIX ICI) ─────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // IMPORTANT
                contentAlignment = Alignment.Center
            ) {

                if (uiState.expenses.isEmpty()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("?", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Aucune dépense ce mois-ci",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Appuyez sur + pour commencer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                } else {

                    val catMap = uiState.categories.associateBy { it.id }

                    LazyColumn {
                        items(uiState.expenses, key = { it.id }) { expense ->
                            ExpenseItem(
                                expense = expense,
                                category = catMap[expense.categoryId],
                                isTopExpense = expense.id == topExpenseId,
                                onClick = { onViewDetail(expense) },
                                onEdit = { onEditExpense(expense) },
                                onDelete = { showDeleteDialog = expense }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    // ── Dialog suppression ───────────────────────────
    showDeleteDialog?.let { expense ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Supprimer la dépense ?") },
            text = { Text("Cette action est irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(expense)
                    showDeleteDialog = null
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}