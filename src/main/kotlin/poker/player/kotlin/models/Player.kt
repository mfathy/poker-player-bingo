package models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    @SerialName("bet")
    val bet: Int,
    @SerialName("hole_cards")
    val holeCards: List<Card?>?,
    @SerialName("id")
    val id: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("stack")
    val stack: Int?,
    @SerialName("status")
    val status: String?,
    @SerialName("version")
    val version: String?
)