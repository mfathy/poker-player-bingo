package models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    @SerialName("rank")
    val rank: String,

    @SerialName("suit")
    val suit: String
)