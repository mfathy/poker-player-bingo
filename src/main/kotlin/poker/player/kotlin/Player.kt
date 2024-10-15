package poker.player.kotlin

import models.GameState

class Player {
    fun betRequest(gameState: GameState): Int {
        println(gameState.toString())
        return 0
    }

    fun showdown() {
    }

    fun version(): String {
        return "Kotlin Player 0.0.1"
    }
}
