package com.smartbudget.app.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D1F5E),
                        Color(0xFF4A3880),
                        Color(0xFF6650A4)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // ── LOGO ANIMÉ ──
            Box(
                modifier = Modifier
                    .scale(scale)
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A3880)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5D4A96)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(48.dp)
                        ) {
                            Box(modifier = Modifier.width(10.dp).height(24.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFFFEAA7)))
                            Box(modifier = Modifier.width(10.dp).height(36.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF96CEB4)))
                            Box(modifier = Modifier.width(10.dp).height(28.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFFF6B6B)))
                            Box(modifier = Modifier.width(10.dp).height(44.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF4ECDC4)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "MAD",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFEAA7),
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // ── NOM DE L'APP — "Lo'" mis en valeur ──
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                // "Lo'" dans un badge doré
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEAA7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Lo'",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3880),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SmartBudget",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            // ── SLOGAN ──
            Text(
                text = "Gérez votre argent,\nmaîtrisez votre avenir.",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.80f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── FEATURES avec icônes Material ──
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FeatureRow(
                    icon = Icons.Filled.BarChart,
                    iconColor = Color(0xFF4ECDC4),
                    text = "Suivez vos dépenses par catégorie"
                )
                FeatureRow(
                    icon = Icons.Filled.CalendarMonth,
                    iconColor = Color(0xFFFFEAA7),
                    text = "Analysez vos habitudes mensuelles"
                )
                FeatureRow(
                    icon = Icons.Filled.FileDownload,
                    iconColor = Color(0xFF96CEB4),
                    text = "Exportez vos données en CSV"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── BOUTON COMMENCER ──
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEAA7),
                    contentColor = Color(0xFF4A3880)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "Commencer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Filled.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "100% hors connexion · Gratuit",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.45f)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    iconColor: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Cercle coloré avec icône Material
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.90f),
            fontWeight = FontWeight.Medium
        )
    }
}