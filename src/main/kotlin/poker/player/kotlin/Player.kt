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

        val badHand = currentPlayer.holeCards?.map { card -> rankToInt(card.rank) }?.sorted()

        val straightCards = copyCards?.map { card -> rankToInt(card.rank) }?.sorted()

        val hasStraight = hasStraight(straightCards, 5)

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

    private fun hasStraight(cards: List<Int>?, numberOfCards: Int): Boolean {
        if (cards.isNullOrEmpty()) return false
        if (cards.size < numberOfCards) return false

        // Validate that all cards are within the range of 1 to 13 (standard poker card values)
        if (cards.any { it !in 1..13 }) throw IllegalArgumentException("Card values must be between 1 and 13")

        // Sort and remove duplicates
        val sortedCards = cards.toSet().sorted()

        // Check for consecutive cards, considering Ace as both 1 and 14
        var consecutiveCount = 1
        for (i in 1 until sortedCards.size) {
            if (sortedCards[i] == sortedCards[i - 1] + 1) {
                consecutiveCount++
                if (consecutiveCount == numberOfCards) return true
            } else {
                consecutiveCount = 1
            }
        }

        // Special check for Ace being treated as 14
        if (sortedCards.contains(1)) {
            // Consider Ace as 14 and check for a straight from 10 to Ace
            val aceAs14 = sortedCards.toMutableList()
            aceAs14.add(14) // Treat Ace as 14
            aceAs14.sort()

            consecutiveCount = 1
            for (i in 1 until aceAs14.size) {
                if (aceAs14[i] == aceAs14[i - 1] + 1) {
                    consecutiveCount++
                    if (consecutiveCount == numberOfCards) return true
                } else {
                    consecutiveCount = 1
                }
            }
        }

        return false
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
