package com.example.museumgame.model

object ExhibitCatalog {
    val orderedExhibits = listOf(
        Exhibit(
            id = ExhibitIds.REAPPEARING_PEN,
            name = "The Reappearing Pen",
            description = "The pen vanishes from its case, then quietly reappears.",
            isAnomaly = true
        ),
        Exhibit(
            id = ExhibitIds.SLIGHTLY_WRONG,
            name = "Slightly Wrong",
            description = "Familiar details have been remembered almost, but not quite, correctly.",
            isAnomaly = true
        )
    )
}
