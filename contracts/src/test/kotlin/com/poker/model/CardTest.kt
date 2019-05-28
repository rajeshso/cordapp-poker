package com.poker.model

import org.junit.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo

class CardTest {
    @Test
    fun testEquals() {
        // Same instance
        val cardA = Card(CardSuitEnum.SPADES, CardRankEnum.CARD_2)
        assertThat(cardA).isEqualTo(cardA)
        assertThat(cardA.hashCode()).isEqualTo(cardA.hashCode())
        // Same suit and rank
        val cardB = Card(CardSuitEnum.SPADES, CardRankEnum.CARD_2)
        assertThat(cardA).isEqualTo(cardB)
        assertThat(cardA.hashCode()).isEqualTo(cardB.hashCode())
        // Different rank
        val cardC = Card(CardSuitEnum.SPADES, CardRankEnum.CARD_3)
        assertThat(cardA).isNotEqualTo(cardC)
        assertThat(cardA.hashCode()).isNotEqualTo(cardC.hashCode())
        // Different suit
        val cardD = Card(CardSuitEnum.CLUBS, CardRankEnum.CARD_2)
        assertThat(cardA).isNotEqualTo(cardD)
        assertThat(cardA.hashCode()).isNotEqualTo(cardD.hashCode())

        // Different suit and rank
        val cardE = Card(CardSuitEnum.HEARTS, CardRankEnum.KING)
        assertThat(cardA).isNotEqualTo(cardE)
        assertThat(cardA.hashCode()).isNotEqualTo(cardE.hashCode())
    }
}