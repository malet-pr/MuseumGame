package com.example.museumgame.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.museumgame.R
import com.example.museumgame.model.Exhibit

@Composable
fun MuseumHallContent(
    exhibits: List<Exhibit>,
    onOpenExhibit: (Exhibit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.museum_hall_title))
        Image(
            painter = painterResource(R.drawable.museum_hall),
            contentDescription = stringResource(R.string.museum_hall_description),
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = 420.dp),
            contentScale = ContentScale.Crop
        )

        exhibits.forEach { exhibit ->
            Button(onClick = { onOpenExhibit(exhibit) }) {
                Text(
                    stringResource(
                        R.string.open_exhibit,
                        stringResource(exhibitNameResource(exhibit.id))
                    )
                )
            }
        }
    }
}
