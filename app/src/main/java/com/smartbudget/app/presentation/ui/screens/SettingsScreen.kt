package com.smartbudget.app.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartbudget.app.presentation.viewmodel.SettingsViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState    by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget  by remember {
        mutableStateOf<com.smartbudget.app.data.local.entity.CategoryEntity?>(null)
    }
    val currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // ── Section Apparence ─────────────────────────────────────────────
        item {
            Text(
                "Apparence",
                style = MaterialTheme.typography.titleMedium,
                color  = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode
                        else            Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text  = if (isDarkMode) "Thème sombre" else "Thème clair",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text  = "Changer l'apparence de l'application",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked         = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() }
                    )
                }
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }

        // ── Section Catégories ────────────────────────────────────────────
        item {
            Text(
                "Catégories",
                style = MaterialTheme.typography.titleMedium,
                color  = MaterialTheme.colorScheme.primary
            )
        }

        items(uiState.categories) { category ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(category.icon, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        category.name,
                        style    = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked         = category.isActive,
                        onCheckedChange = { viewModel.toggleCategory(category) }
                    )
                    IconButton(onClick = { deleteTarget = category }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick  = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ajouter une catégorie")
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }

        // ── Section Export ────────────────────────────────────────────────
        item {
            Text(
                "Export",
                style = MaterialTheme.typography.titleMedium,
                color  = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Button(
                onClick  = { viewModel.exportCsv(currentMonth) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📥 Exporter le mois en CSV")
            }
        }

        // Message export succès
        uiState.exportMessage?.let { msg ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        msg,
                        modifier = Modifier.padding(12.dp),
                        style    = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Message erreur
        uiState.errorMessage?.let { err ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        err,
                        modifier = Modifier.padding(12.dp),
                        color    = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // ── Section Budget global ─────────────────────────────────────────
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }

        item {
            Text(
                "Budget global",
                style = MaterialTheme.typography.titleMedium,
                color  = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Limite mensuelle : 4 000 MAD",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Une notification est envoyée automatiquement " +
                                "lorsque le total du mois dépasse 4 000 MAD.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // ── Dialogue ajout catégorie ──────────────────────────────────────────
    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newIcon by remember { mutableStateOf("📦") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nouvelle catégorie") },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value         = newName,
                        onValueChange = { newName = it },
                        label         = { Text("Nom") },
                        modifier      = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value         = newIcon,
                        onValueChange = { newIcon = it },
                        label         = { Text("Icône (emoji)") },
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        viewModel.addCategory(newName, newIcon, "#888888")
                        showAddDialog = false
                    }
                }) { Text("Ajouter") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Annuler") }
            }
        )
    }

    // ── Dialogue suppression catégorie ────────────────────────────────────
    deleteTarget?.let { cat ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Supprimer ${cat.name} ?") },
            text  = { Text("Impossible si des dépenses y sont associées.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(cat)
                    deleteTarget = null
                }) { Text("Supprimer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Annuler") }
            }
        )
    }
}