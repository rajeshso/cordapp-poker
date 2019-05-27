package com.poker.states

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test

//TODO: Mockito the party and remove the Corda Mocknetwork
class DeckTests {
    private val network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
            TestCordapp.findCordapp("com.poker.contracts")
    )))
    private val dealer = network.createNode()

    var deck = Deck.newShuffledDeck(dealer.info.legalIdentities.first())

    @Before
    fun setup(): Unit {
        network.runNetwork()
        deck = Deck.newShuffledDeck(dealer.info.legalIdentities.first())
        network.startNodes()
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

    @After
    fun close() {
        network.stopNodes()
    }
}


