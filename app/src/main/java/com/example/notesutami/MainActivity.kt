package com.example.notesutami

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesutami.model.NoteViewModel
import com.example.notesutami.ui.theme.NotesUtamiTheme
import com.example.notesutami.ui.theme.ThemeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import com.example.notesutami.ui.NoteScreen
import androidx.compose.foundation.isSystemInDarkTheme


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val noteViewModel: NoteViewModel = viewModel(factory = ViewModelFactory(application))

            // Ambil sistem dark theme (android device)
            val systemDarkTheme = isSystemInDarkTheme()

            NotesUtamiTheme(darkTheme = themeViewModel.isDarkTheme.value) {
                val darkTheme = themeViewModel.isDarkTheme.value

                val view = LocalView.current

                // SideEffect supaya bisa akses window dan set status bar appearance
                SideEffect {
                    val window = this@MainActivity.window
                    WindowCompat.setDecorFitsSystemWindows(window, false)

                    if (darkTheme && !systemDarkTheme) {
                        // Mode dark app tapi sistem android light -> status bar & nav bar tetap terang (ikon putih)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            window.insetsController?.setSystemBarsAppearance(
                                0,
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            window.decorView.systemUiVisibility =
                                window.decorView.systemUiVisibility and
                                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
                                        android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                        }
                    } else {
                        // Kalau light mode app atau sistem dark mode android, biarkan status bar dan nav bar gelap (ikon hitam)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            window.insetsController?.setSystemBarsAppearance(
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            window.decorView.systemUiVisibility =
                                window.decorView.systemUiVisibility or
                                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                                        android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopAppBar(
                            title = { Text("NotesApp") },
                            actions = {
                                IconButton(onClick = { themeViewModel.toggleTheme() }) {
                                    Icon(
                                        imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Toggle Theme"
                                    )
                                }
                            }
                        )
                        NoteScreen(viewModel = noteViewModel)
                    }
                }
            }
        }
    }
}
