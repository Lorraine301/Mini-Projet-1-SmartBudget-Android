package com.smartbudget.app.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbudget.app.data.local.entity.CategoryEntity
import com.smartbudget.app.data.local.entity.ExpenseEntity
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    expense: ExpenseEntity,
    category: CategoryEntity?,
    isTopExpense: Boolean = false,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)

    // Animation badge "top dépense"
    val infiniteTransition = rememberInfiniteTransition(label = "badge")
    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "badgeScale"
    )
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    val catColor = runCatching {
        Color(android.graphics.Color.parseColor(category?.color ?: "#6650A4"))
    }.getOrElse { Color(0xFF6650A4) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail de la dépense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Modifier")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── En-tête colorée ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                catColor.copy(alpha = 0.85f),
                                catColor.copy(alpha = 0.40f)
                            )
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Badge "Dépense la plus chère" animé
                    if (isTopExpense) {
                        Box(
                            modifier = Modifier
                                .scale(badgeScale)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFFEAA7))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🏆", fontSize = 14.sp)
                                Text(
                                    "Dépense la plus élevée du mois",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A3880)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Icône catégorie
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(category?.icon ?: "📦", fontSize = 38.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    // Montant
                    Text(
                        text = "${String.format("%.2f", expense.amount)} ${expense.currency}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Catégorie
                    Text(
                        text = category?.name ?: "Inconnu",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Cartes d'informations ──
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Date
                DetailCard(
                    icon = Icons.Default.CalendarToday,
                    label = "Date",
                    value = expense.date.format(dateFormatter)
                        .replaceFirstChar { it.uppercase() },
                    color = catColor
                )

                // Méthode de paiement
                if (!expense.paymentMethod.isNullOrBlank()) {
                    val payIcon = when (expense.paymentMethod) {
                        "Carte"   -> Icons.Default.CreditCard
                        "Virement"-> Icons.Default.SwapHoriz
                        else      -> Icons.Default.Payments
                    }
                    DetailCard(
                        icon = payIcon,
                        label = "Méthode de paiement",
                        value = expense.paymentMethod,
                        color = catColor
                    )
                }

                // Récurrence
                if (expense.isRecurring) {
                    DetailCard(
                        icon = Icons.Default.Repeat,
                        label = "Type",
                        value = "Dépense récurrente",
                        color = Color(0xFF4ECDC4)
                    )
                }

                // Note complète
                if (!expense.note.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(catColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Notes,
                                        contentDescription = null,
                                        tint = catColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    "Note",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = expense.note,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // Dates système
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(1.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        Text(
                            "Créée le ${expense.createdAt.format(dtFormatter)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Modifiée le ${expense.updatedAt.format(dtFormatter)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null,
                    tint = color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium)
            }
        }
    }
}