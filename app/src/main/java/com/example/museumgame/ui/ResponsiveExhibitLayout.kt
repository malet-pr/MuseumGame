package com.example.museumgame.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun ResponsiveExhibitLayout(
    @StringRes titleResource: Int,
    @DrawableRes illustrationResource: Int,
    @StringRes illustrationDescriptionResource: Int,
    modifier: Modifier = Modifier,
    interactiveContent: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth >= WIDE_EXHIBIT_LAYOUT_MIN_WIDTH) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExhibitTitle(titleResource)
                    ExhibitIllustration(
                        illustrationResource = illustrationResource,
                        descriptionResource = illustrationDescriptionResource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = interactiveContent
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExhibitTitle(titleResource)
                ExhibitIllustration(
                    illustrationResource = illustrationResource,
                    descriptionResource = illustrationDescriptionResource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(maxHeight = 420.dp)
                )
                interactiveContent()
            }
        }
    }
}

@Composable
private fun ExhibitTitle(@StringRes titleResource: Int) {
    Text(
        stringResource(titleResource),
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun ExhibitIllustration(
    @DrawableRes illustrationResource: Int,
    @StringRes descriptionResource: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(illustrationResource),
        contentDescription = stringResource(descriptionResource),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

private val WIDE_EXHIBIT_LAYOUT_MIN_WIDTH = 600.dp
