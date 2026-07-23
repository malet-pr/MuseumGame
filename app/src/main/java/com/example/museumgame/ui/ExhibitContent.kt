package com.example.museumgame.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import com.example.museumgame.viewmodel.MuseumGameViewModel

@Composable
fun ExhibitContent(
    exhibit: Exhibit,
    attempts: Int,
    solved: Boolean,
    onInspect: (Exhibit) -> Unit,
    onRestart: () -> Unit,
    onReturnToHall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(exhibitNameResource(exhibit.id)))
        Image(
            painter = painterResource(exhibitImageResource(exhibit.id)),
            contentDescription = stringResource(R.string.reappearing_pen_image_description),
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = 420.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            if (attempts == 0) {
                stringResource(R.string.initial_museum_message)
            } else {
                stringResource(exhibitDescriptionResource(exhibit.id))
            }
        )
        Text(stringResource(R.string.inspections, attempts))

        Button(
            enabled = !solved,
            onClick = { onInspect(exhibit) }
        ) {
            Text(stringResource(R.string.inspect_exhibit))
        }

        if (solved) {
            Text(stringResource(R.string.room_solved, attempts))
        }

        Button(onClick = onRestart) {
            Text(stringResource(R.string.restart))
        }
        Button(onClick = onReturnToHall) {
            Text(stringResource(R.string.return_to_hall))
        }
    }
}

@DrawableRes
private fun exhibitImageResource(exhibitId: String): Int = when (exhibitId) {
    MuseumGameViewModel.REAPPEARING_PEN_ID -> R.drawable.pen_reappears
    else -> error("No image resource mapped for exhibit ID: $exhibitId")
}

@StringRes
internal fun exhibitNameResource(exhibitId: String): Int = when (exhibitId) {
    MuseumGameViewModel.REAPPEARING_PEN_ID -> R.string.reappearing_pen_name
    else -> error("No name resource mapped for exhibit ID: $exhibitId")
}

@StringRes
private fun exhibitDescriptionResource(exhibitId: String): Int = when (exhibitId) {
    MuseumGameViewModel.REAPPEARING_PEN_ID -> R.string.reappearing_pen_description
    else -> error("No description resource mapped for exhibit ID: $exhibitId")
}
