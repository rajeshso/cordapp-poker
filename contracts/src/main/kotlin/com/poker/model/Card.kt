package com.poker.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class Card(val suit: CardSuitEnum, val rank: CardRankEnum) {
    fun getRankToInt(): Int = rank.ordinal

    override fun toString(): String {
        return "Suit: $suit, Rank :$rank"
    }

    override fun hashCode(): Int {
        return (suit.ordinal * 10 + rank.ordinal)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        } else if (obj !is Card) {
            return false
        } else {
            val card2 = obj as Card?
            return rank.equals(card2!!.rank) && suit.equals(card2.suit)
        }
    }
}