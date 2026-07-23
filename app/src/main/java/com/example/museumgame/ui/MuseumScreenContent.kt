package com.example.museumgame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.museumgame.model.Exhibit

@Composable
fun MuseumScreenContent(
    exhibits: List<Exhibit>,
    message: String,
    attempts: Int,
    solved: Boolean,
    onInspect: (Exhibit) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("MuseumGame")
        Text(message)
        Text("Inspections: $attempts")

        exhibits.forEach { exhibit ->
            Button(
                enabled = !solved,
                onClick = {
                    onInspect(exhibit)
                }
            ) {
                Text("Inspect ${exhibit.name}")
            }
        }

        if (solved) {
            Text("You solved the room in $attempts inspections.")
        }

        Button(
            onClick = onRestart
        ) {
            Text("Restart")
        }
    }
}