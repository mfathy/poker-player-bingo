package poker.player.kotlin

import models.GameState

const val MAX_BET = 100

class Player {
    fun betRequest(gameState: GameState): Int {
        val bet = gameState.currentBuyIn - gameState.players[gameState.inAction].bet
        return if (gameState.currentBuyIn > MAX_BET) 0 else bet
    }

    fun showdown() {
    }

    fun version(): String {
        return "Kotlin Player 0.0.1"
    }
}
