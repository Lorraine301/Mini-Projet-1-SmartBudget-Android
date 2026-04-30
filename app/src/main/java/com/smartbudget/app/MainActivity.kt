package com.smartbudget.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.smartbudget.app.presentation.ui.navigation.SmartBudgetNavGraph
import com.smartbudget.app.presentation.ui.theme.SmartBudgetTheme
import com.smartbudget.app.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    // Launcher pour demander la permission notifications
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "Permission notifications accordée")
        } else {
            android.util.Log.w("MainActivity", "Permission notifications refusée")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Demander la permission sur Android 13+
        requestNotificationPermission()

        setContent {
            val isDark by settingsViewModel.isDarkMode.collectAsState()
            SmartBudgetTheme(darkTheme = isDark) {
                SmartBudgetNavGraph(settingsViewModel = settingsViewModel)
            }
        }
    }

    private fun requestNotificationPermission() {
        // POST_NOTIFICATIONS requis uniquement sur Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                // Permission déjà accordée
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    android.util.Log.d("MainActivity", "Permission déjà accordée")
                }
                // Demander la permission
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
        // Sur Android 12 et inférieur : pas besoin de demander
    }
}