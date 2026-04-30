package com.smartbudget.app.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartbudget.app.data.local.entity.CategoryEntity
import com.smartbudget.app.data.local.entity.ExpenseEntity
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseItem(
    expense: ExpenseEntity,
    category: CategoryEntity?,
    isTopExpense: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

    // Animation étoile top dépense
    val infiniteTransition = rememberInfiniteTransition(label = "star")
    val starScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "starScale"
    )

    val catColor = runCatching {
        Color(android.graphics.Color.parseColor(category?.color ?: "#B0B0B0"))
    }.getOrElse { Color.Gray }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTopExpense) 6.dp else 2.dp
        ),
        border = if (isTopExpense)
            androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFEAA7))
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône catégorie
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(catColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(category?.icon ?: "📦",
                    style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.width(12.dp))

            // Infos
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = category?.name ?: "Inconnu",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Badge top dépense animé
                    if (isTopExpense) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Top dépense",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp).scale(starScale)
                        )
                    }
                    if (expense.isRecurring) {
                        Text("🔄", fontSize = 11.sp)
                    }
                }
                // Note tronquée
                if (!expense.note.isNullOrBlank()) {
                    Text(
                        text = expense.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = expense.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Montant
            Text(
                text = "${String.format("%.2f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isTopExpense) Color(0xFFFFB300)
                else MaterialTheme.colorScheme.primary
            )
            Text(
                text = " MAD",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(6.dp))

            // Actions
            Column {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}