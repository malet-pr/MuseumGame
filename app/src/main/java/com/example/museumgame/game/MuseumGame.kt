package com.example.museumgame.game;

import com.example.museumgame.model.Exhibit;

class MuseumGame(
        val exhibits:List<Exhibit>
) {
    var attempts: Int = 0
    private set

    var solved: Boolean = false
    private set

    fun inspect(exhibit: Exhibit): String {
        if (solved) {
            return "The room is already solved."
        }

        attempts++

        if (exhibit.isAnomaly) {
            solved = true
        }

        return exhibit.description
    }

    fun restart() {
        attempts = 0
        solved = false
    }
}
