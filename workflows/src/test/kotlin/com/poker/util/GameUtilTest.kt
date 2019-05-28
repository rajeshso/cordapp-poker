package com.poker.util

import org.junit.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.poker.model.Card
import com.poker.model.CardRankEnum.*
import com.poker.model.CardSuitEnum.*
import com.poker.model.Player
import com.poker.states.Deck
import net.corda.core.identity.Party
import org.mockito.Mockito

class GameUtilTest {
    val mockParty = Mockito.mock(Party::class.java)

    var deck = Deck(mockParty)

/*    @Test
    fun testDrawGameTwoPlayers() {
        //Basic tests
        val game = GameUtil
        val player = Player(mockParty)
        val dealer = Player(mockParty)
        val tableCards = emptyList<Card>()
        game.newGame(Deck(), player, dealer)
        game.deal()
        game.callFlop()
        game.betRiver()
        game.betTurn()
        tableCards.add(Card(SPADES, CARD_10))
        game.getTableCards().add(Card(SPADES, ACE))
        dealer.getCards()[0] = Card(DIAMONDS, CARD_2)
        dealer.getCards()[1] = Card(SPADES, CARD_2)
        player.getCards()[0] = Card(CLUBS, CARD_2)
        player.getCards()[1] = Card(HEARTS, CARD_2)
        val winnerList = game.getWinner()
        assertEquals(2, winnerList.size)
        assertEquals(ONE_PAIR, dealer.getRankingEnum())
        assertEquals(ONE_PAIR, player.getRankingEnum())
    }*/
}