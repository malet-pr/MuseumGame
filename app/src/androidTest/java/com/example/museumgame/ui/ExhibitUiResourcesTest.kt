package com.example.museumgame.ui

import com.example.museumgame.R
import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitCatalog
import com.example.museumgame.model.ExhibitIds
import org.junit.Assert.assertEquals
import org.junit.Test

class ExhibitUiResourcesTest {

    @Test
    fun everyCatalogEntryHasItsExactResourceMapping() {
        val expectedById = mapOf(
            ExhibitIds.REAPPEARING_PEN to ExhibitUiResources(
                screen = ExhibitUiScreen.REAPPEARING_PEN,
                nameResource = R.string.reappearing_pen_name,
                illustrationResource = R.drawable.pen_reappears,
                illustrationDescriptionResource =
                    R.string.reappearing_pen_image_description
            ),
            ExhibitIds.SLIGHTLY_WRONG to ExhibitUiResources(
                screen = ExhibitUiScreen.SLIGHTLY_WRONG,
                nameResource = R.string.slightly_wrong_name,
                illustrationResource = R.drawable.slightly_wrong,
                illustrationDescriptionResource =
                    R.string.slightly_wrong_image_description
            ),
            ExhibitIds.WORK_APPARENT to ExhibitUiResources(
                screen = ExhibitUiScreen.WORK_APPARENT,
                nameResource = R.string.work_apparent_name,
                illustrationResource = R.drawable.work_apparent,
                illustrationDescriptionResource =
                    R.string.work_apparent_image_description
            ),
            ExhibitIds.SIMULATED_PROGRESS to ExhibitUiResources(
                screen = ExhibitUiScreen.SIMULATED_PROGRESS,
                nameResource = R.string.simulated_progress_name,
                illustrationResource = R.drawable.simulated_progress,
                illustrationDescriptionResource =
                    R.string.simulated_progress_image_description
            ),
            ExhibitIds.NEAR_OCCURRENCE to ExhibitUiResources(
                screen = ExhibitUiScreen.NEAR_OCCURRENCE,
                nameResource = R.string.near_occurrence_name,
                illustrationResource = R.drawable.almost_happened,
                illustrationDescriptionResource =
                    R.string.near_occurrence_image_description
            ),
            ExhibitIds.CREATIVE_CHAOS to ExhibitUiResources(
                screen = ExhibitUiScreen.CREATIVE_CHAOS,
                nameResource = R.string.creative_chaos_name,
                illustrationResource = R.drawable.creative_chaos,
                illustrationDescriptionResource =
                    R.string.creative_chaos_image_description
            )
        )
        val catalogIds = ExhibitCatalog.orderedExhibits.map(Exhibit::id)

        assertEquals(expectedById.keys.toList(), catalogIds)
        expectedById.forEach { (exhibitId, expectedResources) ->
            assertEquals(expectedResources, exhibitUiResources(exhibitId))
        }
    }
}
