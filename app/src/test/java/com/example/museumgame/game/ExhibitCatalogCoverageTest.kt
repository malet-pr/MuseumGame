package com.example.museumgame.game

import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ExhibitCatalogCoverageTest {

    @Test
    fun catalogIdsAreUniqueAndDriveMuseumOrder() {
        val catalogIds = ExhibitCatalog.orderedExhibits.map(Exhibit::id)
        val game = MuseumGame()

        assertEquals(catalogIds.size, catalogIds.toSet().size)
        assertEquals(catalogIds, game.orderedExhibitIds)
    }

    @Test
    fun catalogContainsSixPlayableExhibitsAndExcludesKubernetesCityFinale() {
        val catalogIds = ExhibitCatalog.orderedExhibits.map(Exhibit::id)

        assertEquals(6, catalogIds.size)
        assertFalse("kubernetes_city" in catalogIds)
    }

    @Test
    fun everyCatalogEntryHasCompletionProgressAndRestartHandling() {
        val game = MuseumGame()

        game.orderedExhibitIds.forEach { exhibitId ->
            assertFalse(game.isCompleted(exhibitId))
            assertEquals(ExhibitProgress(), game.progressFor(exhibitId))
            game.restartExhibit(exhibitId)
        }
    }
}
