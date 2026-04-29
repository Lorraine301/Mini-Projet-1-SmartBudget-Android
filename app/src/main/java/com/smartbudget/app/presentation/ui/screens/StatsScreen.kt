package com.smartbudget.app.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartbudget.app.presentation.ui.components.MonthNavigator
import com.smartbudget.app.presentation.viewmodel.StatsViewModel

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MonthNavigator(
            selectedMonth = uiState.selectedMonth,
            onPrevious = viewModel::goToPreviousMonth,
            onNext = viewModel::goToNextMonth,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total du mois
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total du mois", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${String.format("%.2f", uiState.totalMonth)} MAD",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (uiState.totalPreviousMonth > 0) {
                            val diff = uiState.totalMonth - uiState.totalPreviousMonth
                            val pct = (diff / uiState.totalPreviousMonth) * 100
                            val sign = if (diff >= 0) "+" else ""
                            Text(
                                "$sign${String.format("%.1f", pct)}% vs mois précédent",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (diff <= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                    }
                }
            }

            // Répartition par catégorie
            item {
                Text(
                    "Répartition par catégorie",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.categoryStats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucune donnée pour ce mois",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(uiState.categoryStats) { stat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(stat.category.icon,
                                        style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stat.category.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "${String.format("%.2f", stat.total)} MAD",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "${String.format("%.1f", stat.percentage)}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Barre de progression
                            val barColor = runCatching {
                                Color(android.graphics.Color.parseColor(stat.category.color))
                            }.getOrElse { MaterialTheme.colorScheme.primary }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(
                                            (stat.percentage / 100f).toFloat().coerceIn(0f, 1f)
                                        )
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(barColor)
                                )
                            }

                            // Budget si défini
                            stat.budget?.let { budget ->
                                Spacer(Modifier.height(4.dp))
                                val remaining = budget.limitAmount - stat.total
                                Text(
                                    "Budget : ${String.format("%.0f", budget.limitAmount)} MAD" +
                                            " | Reste : ${String.format("%.0f", remaining)} MAD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (remaining >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}