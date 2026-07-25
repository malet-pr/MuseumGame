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
        ),
        Exhibit(
            id = ExhibitIds.WORK_APPARENT,
            name = "Work Apparent",
            description = "A paper trail produces endless activity while every task returns to the inbox.",
            isAnomaly = true
        ),
        Exhibit(
            id = ExhibitIds.SIMULATED_PROGRESS,
            name = "Simulated Progress",
            description = "A busy workshop confuses effort and deliverables with meaningful change.",
            isAnomaly = true
        )
    )
}
