package com.example.museumgame.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.museumgame.R
import com.example.museumgame.model.ExhibitIds

internal enum class ExhibitUiScreen {
    REAPPEARING_PEN,
    SLIGHTLY_WRONG,
    WORK_APPARENT,
    SIMULATED_PROGRESS,
    NEAR_OCCURRENCE
}

internal data class ExhibitUiResources(
    val screen: ExhibitUiScreen,
    @param:StringRes val nameResource: Int,
    @param:DrawableRes val illustrationResource: Int,
    @param:StringRes val illustrationDescriptionResource: Int
)

internal fun exhibitUiResources(exhibitId: String): ExhibitUiResources = when (exhibitId) {
    ExhibitIds.REAPPEARING_PEN -> ExhibitUiResources(
        screen = ExhibitUiScreen.REAPPEARING_PEN,
        nameResource = R.string.reappearing_pen_name,
        illustrationResource = R.drawable.pen_reappears,
        illustrationDescriptionResource = R.string.reappearing_pen_image_description
    )

    ExhibitIds.SLIGHTLY_WRONG -> ExhibitUiResources(
        screen = ExhibitUiScreen.SLIGHTLY_WRONG,
        nameResource = R.string.slightly_wrong_name,
        illustrationResource = R.drawable.slightly_wrong,
        illustrationDescriptionResource = R.string.slightly_wrong_image_description
    )

    ExhibitIds.WORK_APPARENT -> ExhibitUiResources(
        screen = ExhibitUiScreen.WORK_APPARENT,
        nameResource = R.string.work_apparent_name,
        illustrationResource = R.drawable.work_apparent,
        illustrationDescriptionResource = R.string.work_apparent_image_description
    )

    ExhibitIds.SIMULATED_PROGRESS -> ExhibitUiResources(
        screen = ExhibitUiScreen.SIMULATED_PROGRESS,
        nameResource = R.string.simulated_progress_name,
        illustrationResource = R.drawable.simulate_progress,
        illustrationDescriptionResource = R.string.simulated_progress_image_description
    )

    ExhibitIds.NEAR_OCCURRENCE -> ExhibitUiResources(
        screen = ExhibitUiScreen.NEAR_OCCURRENCE,
        nameResource = R.string.near_occurrence_name,
        illustrationResource = R.drawable.near_occurrence,
        illustrationDescriptionResource = R.string.near_occurrence_image_description
    )

    else -> error("No UI resources mapped for exhibit ID: $exhibitId")
}
