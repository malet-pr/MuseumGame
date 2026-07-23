package com.example.museumgame

import com.example.museumgame.ui.MuseumScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.museumgame.model.Exhibit
import com.example.museumgame.ui.MuseumScreenContent
import com.example.museumgame.ui.theme.MuseumGameTheme
import com.example.museumgame.viewmodel.MuseumGameViewModel

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



@Preview(showBackground = true)
@Composable
fun MuseumScreenPreview() {
    MuseumGameTheme {
        MuseumScreenContent(
            exhibits = listOf(
                Exhibit(
                    name = "Ancient vase",
                    description = "Nothing suspicious.",
                    isAnomaly = false
                ),
                Exhibit(
                    name = "Portrait",
                    description = "The portrait blinked.",
                    isAnomaly = true
                ),
                Exhibit(
                    name = "Roman coin",
                    description = "A normal Roman coin.",
                    isAnomaly = false
                )
            ),
            message = "One exhibit does not belong. Inspect the museum.",
            attempts = 0,
            solved = false,
            onInspect = {},
            onRestart = {}
        )
    }
}