package poker.player.kotlin

import models.GameState

const val MAX_BET = 1000

class Player {
    fun betRequest(gameState: GameState): Int {
        val ourPlayer = gameState.players[gameState.inAction]

        val callBet = gameState.currentBuyIn - ourPlayer.bet

        if (!hasPair(gameState)) {
            return if (gameState.currentBuyIn > MAX_BET) 0 else callBet
        }

        val raiseBet = callBet + 2 * gameState.minimumRaise
        return raiseBet
    }

    fun hasPair(gameState: GameState): Boolean {
        val cardRanks = mutableListOf<String>()
        val ourPlayer = gameState.players[gameState.inAction]

        ourPlayer.holeCards?.forEach() { card ->
            cardRanks.add(card.rank)
        }

        gameState.communityCards.forEach() { card ->
            cardRanks.add(card.rank)
        }

        val cardRanksSet = cardRanks.toSet()

        return cardRanks.size > cardRanksSet.size

    }

    fun showdown() {
    }

    fun version(): String {
        return "Kotlin Player 0.0.1"
    }
}
