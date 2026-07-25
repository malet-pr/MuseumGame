package com.example.museumgame.ui

import com.example.museumgame.model.Exhibit
import com.example.museumgame.model.ExhibitCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ExhibitUiResourcesTest {

    @Test
    fun everyCatalogEntryHasUniqueScreenAndResourceMapping() {
        val resources = ExhibitCatalog.orderedExhibits
            .map(Exhibit::id)
            .map(::exhibitUiResources)

        assertEquals(ExhibitUiScreen.entries.toSet(), resources.map { it.screen }.toSet())
        assertEquals(resources.size, resources.map { it.screen }.toSet().size)
        resources.forEach {
            assertNotEquals(0, it.nameResource)
            assertNotEquals(0, it.illustrationResource)
            assertNotEquals(0, it.illustrationDescriptionResource)
        }
    }
}
