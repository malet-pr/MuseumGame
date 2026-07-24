package com.example.museumgame.game

data class ExhibitProgress(
    val attempts: Int = 0
)

data class ExhibitVisitStatus(
    val exhibitId: String,
    val completed: Boolean,
    val unlocked: Boolean,
    val current: Boolean
)
