package poker.player.kotlin

import models.Card
import models.GameState
import kotlin.math.min
import kotlin.random.Random

const val MIN_GOOD_HAND = 100

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

        val copyCards = currentPlayer.holeCards?.toMutableList()
        copyCards?.addAll(gameState.communityCards)
        val allCardsCount = copyCards?.map { card: Card ->
            card.rank
        }?.toList()?.count()

        val allCardsAsSetCount = copyCards?.map { card: Card ->
            card.rank
        }?.toSet()?.count()

        val hasPair = allCardsCount != allCardsAsSetCount

        val bluffCity = Random.nextDouble(0.0, 1.0) < bluffProbability

//        val badHand = currentPlayer.holeCards?.map { card -> rankToInt(card.rank) }?.sorted()

        return when {
            hasStrongHand || hasPair || bluffCity -> {
                val raise = callAmount + 2 * gameState.minimumRaise
                return when {
                    raise > currentPlayer.stack!! -> currentPlayer.stack
                    raise + currentPlayer.bet < MIN_GOOD_HAND -> MIN_GOOD_HAND - currentPlayer.bet
                    else -> raise
                }
            }
//            badHand -> {
//                0
//            }
            else -> {
                min(callAmount, currentPlayer.stack!!)
            }
        }
    }

    fun rankToInt(rank: String): Int {
        return when(rank) {
            "J" -> 11
            "Q" -> 12
            "K" -> 13
            "A" -> 14
            else -> rank.toInt()
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
