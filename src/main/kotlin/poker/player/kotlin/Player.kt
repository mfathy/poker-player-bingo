package poker.player.kotlin

import models.Card
import models.GameState
import kotlin.random.Random

const val MAX_BET = 1000

class Player {
    fun betRequest(gameState: GameState): Int {

        if (gameState.inAction == null) return 0
        if (gameState.minimumRaise == null) return 0

        val currentPlayer = gameState.players[gameState.inAction]
        val callAmount = gameState.currentBuyIn - currentPlayer.bet
        val bluffProbability = 0.2 // 20% chance to bluff
        val strongCards = listOf("A", "K", "Q", "J") // High-ranked cards

        // Check if the player has strong hole cards
        val hasStrongHand = currentPlayer.holeCards?.any { card ->
            card.rank in strongCards
        } ?: false

        val hasPairInHand = currentPlayer.holeCards?.map { card -> card.rank }?.toSet()?.size == 1

        val copyCards = currentPlayer.holeCards?.toMutableList()
        copyCards?.addAll(gameState.communityCards)
        val allCardsCount = copyCards?.map { card: Card ->
            card.rank
        }?.toList()?.count()

        val allCardsAsSetCount = copyCards?.map { card: Card ->
            card.rank
        }?.toSet()?.count()

        val hasPair = allCardsCount != allCardsAsSetCount

        return when {
            hasStrongHand || hasPairInHand || hasPair -> {
                // Strong hand: Raise more than the minimum raise
                callAmount + gameState.minimumRaise
            }
            Random.nextDouble(0.0, 1.0) < bluffProbability -> {
                // Bluff: Randomly raise even with a weak hand
                callAmount + gameState.minimumRaise
            }
            currentPlayer.stack!! > callAmount -> {
                // Regular call if we can afford it
                callAmount
            }
            else -> {
                // Fold (bet 0) if we can't meet the call
                0
            }
        }
    }

//    fun hasPair(gameState: GameState): Boolean {
//        val cardRanks = mutableListOf<String>()
//        val ourPlayer = gameState.players[gameState.inAction]
//
//        ourPlayer.holeCards?.forEach() { card ->
//            cardRanks.add(card.rank)
//        }
//
//        gameState.communityCards.forEach() { card ->
//            cardRanks.add(card.rank)
//        }
//
//        val cardRanksSet = cardRanks.toSet()
//
//        return cardRanks.size > cardRanksSet.size
//
//    }

    fun showdown() {
    }

    fun version(): String {
        return "Kotlin Player 0.0.1"
    }
}
