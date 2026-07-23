package com.example.museumgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.museumgame.ui.MuseumScreen
import com.example.museumgame.ui.theme.MuseumGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuseumGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MuseumScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
