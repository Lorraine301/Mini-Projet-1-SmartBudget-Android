package com.smartbudget.app.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartbudget.app.data.local.entity.ExpenseEntity
import com.smartbudget.app.presentation.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expenseToEdit: ExpenseEntity? = null,
    onBack: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = expenseToEdit != null
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var amount by remember { mutableStateOf(expenseToEdit?.amount?.toString() ?: "") }
    var selectedCategoryId by remember { mutableStateOf(expenseToEdit?.categoryId ?: 0L) }
    var date by remember { mutableStateOf(expenseToEdit?.date ?: LocalDate.now()) }
    var note by remember { mutableStateOf(expenseToEdit?.note ?: "") }
    var paymentMethod by remember { mutableStateOf(expenseToEdit?.paymentMethod ?: "") }
    var isRecurring by remember { mutableStateOf(expenseToEdit?.isRecurring ?: false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val paymentMethods = listOf("Espèce", "Carte", "Virement", "Autre")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Modifier la dépense" else "Nouvelle dépense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Montant
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; amountError = null },
                label = { Text("Montant (MAD) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError != null,
                supportingText = amountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Catégorie
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                val selectedCat = uiState.categories.find { it.id == selectedCategoryId }
                OutlinedTextField(
                    value = selectedCat?.let { "${it.icon} ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Catégorie *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    isError = categoryError != null,
                    supportingText = categoryError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    uiState.categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text("${cat.icon} ${cat.name}") },
                            onClick = {
                                selectedCategoryId = cat.id
                                categoryExpanded = false
                                categoryError = null
                            }
                        )
                    }
                }
            }

            // Date
            OutlinedTextField(
                value = date.format(dateFormatter),
                onValueChange = {
                    runCatching {
                        date = LocalDate.parse(it, dateFormatter)
                    }
                },
                label = { Text("Date *") },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("JJ/MM/AAAA") }
            )

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optionnel)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // Méthode de paiement
            ExposedDropdownMenuBox(
                expanded = paymentExpanded,
                onExpandedChange = { paymentExpanded = it }
            ) {
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Méthode de paiement") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = paymentExpanded,
                    onDismissRequest = { paymentExpanded = false }
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = { paymentMethod = method; paymentExpanded = false }
                        )
                    }
                }
            }

            // Dépense récurrente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Dépense récurrente",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                )
                Switch(checked = isRecurring, onCheckedChange = { isRecurring = it })
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton enregistrer
            Button(
                onClick = {
                    val amountVal = amount.toDoubleOrNull()
                    var valid = true
                    if (amountVal == null || amountVal <= 0) {
                        amountError = "Montant invalide (doit être > 0)"
                        valid = false
                    }
                    if (selectedCategoryId == 0L) {
                        categoryError = "Veuillez choisir une catégorie"
                        valid = false
                    }
                    if (valid) {
                        if (isEditing && expenseToEdit != null) {
                            viewModel.updateExpense(
                                expenseToEdit.copy(
                                    amount = amountVal!!,
                                    categoryId = selectedCategoryId,
                                    date = date,
                                    note = note.ifBlank { null },
                                    paymentMethod = paymentMethod.ifBlank { null },
                                    isRecurring = isRecurring
                                )
                            )
                        } else {
                            viewModel.addExpense(
                                amount = amountVal!!,
                                categoryId = selectedCategoryId,
                                date = date,
                                note = note.ifBlank { null },
                                paymentMethod = paymentMethod.ifBlank { null },
                                isRecurring = isRecurring
                            )
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isEditing) "Mettre à jour" else "Enregistrer",
                    style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}