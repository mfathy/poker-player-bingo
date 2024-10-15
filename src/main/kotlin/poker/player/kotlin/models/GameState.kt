package models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    @SerialName("bet_index")
    val betIndex: Int?,

    @SerialName("community_cards")
    val communityCards: List<Card>,
    @SerialName("current_buy_in")
    val currentBuyIn: Int,
    @SerialName("dealer")
    val dealer: Int?,
    @SerialName("game_id")
    val gameId: String?,
    @SerialName("in_action")
    val inAction: Int? = null,
    @SerialName("minimum_raise")
    val minimumRaise: Int? = null,
    @SerialName("orbits")
    val orbits: Int?,
    @SerialName("players")
    val players: List<Player>,
    @SerialName("pot")
    val pot: Int?,
    @SerialName("round")
    val round: Int?,
    @SerialName("small_blind")
    val smallBlind: Int?,
    @SerialName("tournament_id")
    val tournamentId: String?
)