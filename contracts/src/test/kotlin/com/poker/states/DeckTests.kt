package com.poker.states

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import net.corda.core.identity.Party
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


class DeckTests {
    val mockParty = Mockito.mock(Party::class.java)

    var deck = Deck.newShuffledDeck(mockParty)

    @Before
    fun setup(): Unit {
        deck = Deck.newShuffledDeck(mockParty)
    }

    @Test
    fun `deck contains cards`() {
        assertThat(deck.pop()).isNotNull()
    }

    @Test
    fun `deck pops different cards`() {
        assertThat(deck.pop()).isNotEqualTo(deck.pop())
    }

    @Test
    fun `deck pops 52 cards`() {
        repeat(52) {
            var card = deck.pop()
        }
    }

    @Test(expected = NoSuchElementException::class)
    fun `deck throws Exception after 52'nd pop`() {
        repeat(52) {
            var card = deck.pop()
        }
        deck.pop()
    }

    @Test
    fun `deck has the same signature after any number of pops`() {
        val before = deck.signature
        repeat((1..12).shuffled().first()) {
            deck.pop()
        }
        val after = deck.signature
        assertThat(before).isEqualTo(after)
    }
}


