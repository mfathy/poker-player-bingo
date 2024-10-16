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

        val holeCards = currentPlayer.holeCards ?: emptyList()

        val hasStraight = hasStraight(holeCards, gameState.communityCards)

        val stack = currentPlayer.stack ?: 0
        val hasOwnFlush = hasOwnFlush(holeCards, gameState.communityCards)

        return when {
            gameState.orbits == 0 && isBadHand(holeCards) -> {
                println("[${gameState.gameId}] fold: ${currentPlayer.bet}, $callAmount")
                0
            }
            gameState.orbits == 0 && hasPair -> {
                val goodRaise = min(stack - currentPlayer.bet, 200) - currentPlayer.bet
                val pairValue = pairValue(holeCards)
                val action = when(ourPosition(gameState)) {
                    Position.EARLY -> if (pairValue > 10) goodRaise else callAmount
                    Position.MIDDLE, Position.LATE -> if (pairValue > 7) goodRaise else callAmount
                }
                println("[${gameState.gameId}] earlyPair: ${currentPlayer.bet}, $action")
                action
            }
            hasOwnFlush || hasStraight || hasPair || isBluff -> {
                val raise = callAmount + gameState.minimumRaise
                val action = when {
                    currentPlayer.bet > HIGH_BET -> callAmount
                    raise > stack -> stack
                    raise + currentPlayer.bet < MIN_GOOD_HAND -> MIN_GOOD_HAND - currentPlayer.bet
                    else -> raise
                }
                println("[${gameState.gameId}] Raising: $hasStraight, $hasPair, $isBluff; $callAmount, $stack, $raise; $action")
                return action
            }
            !hasStrongHand && ((badHand && callAmount > SMALL_CALL) || callAmount > MAX_CALL) -> {
                println("[${gameState.gameId}] Folding: $badHand, $callAmount")
                0
            }
            callAmount > 200 -> {
                println("[${gameState.gameId}] Folding, too much money: $callAmount")
                0
            }
            else -> {
                println("[${gameState.gameId}] Calling")
                min(callAmount, stack)
            }
        }
    }

    private fun hasOwnFlush(holeCards: List<Card>, communityCards: List<Card>): Boolean {
        val allCards = holeCards + communityCards

        val suitCounts: Map<String, Int> = allCards.groupBy { card -> card.suit }.mapValues { entry -> entry.value.size }

        val flushSuitMaybe = suitCounts.firstNotNullOfOrNull { entry -> if (entry.value >= 5) entry.key else null }

        return flushSuitMaybe != null && holeCards.any { card -> card.suit == flushSuitMaybe }
    }

    private fun pairValue(holeCards: List<Card>): Int {
        val holeRanksSet = holeCards.map { card -> card.rank }.toSet()

        return when {
            holeRanksSet.size != 1 -> 0
            else -> rankToInt(holeRanksSet.first())
        }
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

    private fun hasStraight(holeCards: List<Card>, communityCards: List<Card>): Boolean {
        if (communityCards.size < 3) return false // Impossible to straight before 3 cards

        // Sort and remove duplicates
        val sortedCards = (holeCards + communityCards).map { card -> rankToInt(card.rank) }.toSet().toList().sorted().toMutableList()

        // If we have an ace, also add a 1
        if (sortedCards.last() == 14) sortedCards.apply { add(0, 1) }

        var consecutiveCount = 1
        var previousCard = sortedCards.removeFirst()
        for (cardVal in sortedCards) {
            if (cardVal == previousCard + 1) {
                previousCard = cardVal
                consecutiveCount += 1
                if (consecutiveCount == 5) break;
            } else {
                previousCard = cardVal
                consecutiveCount = 1
            }
        }

        return consecutiveCount >= 5
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
