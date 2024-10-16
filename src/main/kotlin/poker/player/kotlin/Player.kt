package poker.player.kotlin

import models.Card
import models.GameState
import kotlin.math.min
import kotlin.random.Random

const val MIN_GOOD_HAND = 100
const val HIGH_BET = 300
const val MAX_CALL = 200
const val SMALL_CALL = 10

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

        val hasPair = hasPair(currentPlayer.holeCards, gameState.communityCards)

        val random = Random(gameState.gameId.hashCode())
        val isBluff = gameState.communityCards.size >= 4 && random.nextDouble() < bluffProbability


        val badHand = isBadHand(currentPlayer.holeCards)

        val straightCards = currentPlayer.holeCards?.map { card -> rankToInt(card.rank) }?.sorted()

        val hasStraight = hasStraight(straightCards, 5)

        val stack = currentPlayer.stack ?: 0

        return when {
            earlyFold(currentPlayer.holeCards ?: emptyList(), gameState.communityCards) -> {
                println("Early fold: ${currentPlayer.bet}, $callAmount")
                0
            }
            hasStraight || hasPair || isBluff -> {
                println("Raising: $hasStraight, $hasPair, $isBluff")
                val raise = callAmount + gameState.minimumRaise
                return when {
                    currentPlayer.bet > HIGH_BET -> callAmount
                    raise > stack -> stack
                    raise + currentPlayer.bet < MIN_GOOD_HAND -> MIN_GOOD_HAND - currentPlayer.bet
                    else -> raise
                }
            }
            !hasStrongHand && ((badHand && callAmount > SMALL_CALL) || callAmount > MAX_CALL) -> {
                println("Folding: $badHand, $callAmount")
                0
            }
            else -> {
                min(callAmount, stack)
            }
        }
    }

    private fun earlyFold(holeCards: List<Card>, communityCards: List<Card>): Boolean {
        return isBadHand(holeCards) && communityCards.isEmpty()
    }

    private fun hasPair(holeCards: List<Card>?, communityCards: List<Card>?): Boolean {
        val holeRanks = holeCards?.map { it.rank } ?: return false

        if (holeRanks.size < 2) return false  // Ensure at least two cards in holeCards

        val pairInHole = holeRanks[0] == holeRanks[1]

        val communityCardsPairUp = communityCards?.any { card -> card.rank in holeRanks } ?: false

        return pairInHole || communityCardsPairUp
    }

    private fun isBadHand(holeCards: List<Card>?): Boolean {
        // Check for null or invalid number of cards
        if (holeCards == null || holeCards.size < 2) return false

        val (a, b) = holeCards.map { rankToInt(it.rank) }
        val (aSuit, bSuit) = holeCards.map { it.suit }

        // Return if cards are different in rank and suit, and fall within specific ranges
        return a != b && aSuit != bSuit &&
                ((a in 5..11 && b in 2..7) || (b in 5..11 && a in 2..7))
    }


    private fun rankToInt(rank: String): Int {
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

    enum class Position { EARLY, MIDDLE, LATE }

    private fun ourPosition(gameState: GameState): Position {
        val inPlayers = gameState.players.filter { player -> player.status != "out" }
        val playerCount = inPlayers.count()

        val firstPlayerId = ((gameState.dealer ?: 0) + 1) % gameState.players.size
        val ourId = gameState.inAction ?: 0

        val ourRelativePosition = inPlayers.count { player -> player.id in firstPlayerId..ourId } - 1

        return when {
            ourRelativePosition < playerCount / 3 -> Position.EARLY
            ourRelativePosition < 2 * playerCount / 3 -> Position.MIDDLE
            else -> Position.LATE
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
