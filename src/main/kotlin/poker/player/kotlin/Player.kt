package poker.player.kotlin

import models.GameState

const val MAX_BET = 1000

class Player {
    fun betRequest(gameState: GameState): Int {
        val ourPlayer = gameState.players[gameState.inAction]

        val callBet = gameState.currentBuyIn - ourPlayer.bet

        if (ourPlayer.holeCards!![0].rank != ourPlayer.holeCards[1].rank) {
            return if (gameState.currentBuyIn > MAX_BET) 0 else callBet
        }

        val raiseBet = callBet + 2 * gameState.minimumRaise
        return raiseBet
    }

    fun showdown() {
    }

    fun version(): String {
        return "Kotlin Player 0.0.1"
    }
}
