package com.poker.util

import org.junit.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.poker.model.Card
import com.poker.model.CardRankEnum.*
import com.poker.model.CardSuitEnum.*
import com.poker.model.Player
import com.poker.model.RankingEnum.*
import com.poker.states.Deck
import net.corda.core.identity.Party
import org.mockito.Mockito

class GameUtilTest {
    val mockParty = Mockito.mock(Party::class.java)

    var deck = Deck(mockParty)

    @Test
    fun testDrawGameTwoPlayers() {
        //Basic tests
        val game = GameUtil
        val playerA = Player(mockParty)
        val playerB = Player(mockParty)
        val playerList = mutableListOf<Player>(playerA, playerB)
        val dealer =mockParty
        val tableCards = mutableListOf<Card>()
        val deck: Deck = Deck(dealer)

        game.deal(playerList, tableCards, deck)
        game.callFlop(playerList, tableCards, deck)
        game.betRiver(playerList, tableCards, deck)
        game.betTurn(playerList, tableCards, deck)
        tableCards.clear()
        tableCards.add(Card(SPADES, CARD_10))
        tableCards.add(Card(SPADES, ACE))
        playerA.myCards = listOf(Card(DIAMONDS, CARD_2), Card(SPADES, CARD_2))
        playerB.myCards = listOf(Card(CLUBS, CARD_2), Card(HEARTS, CARD_2))
        val winnerList = game.getWinner(playerList, tableCards)
        assertThat(winnerList.size).isEqualTo(2)
        assertThat(playerA.rankingEnum).isEqualTo(ONE_PAIR)
        assertThat(playerB.rankingEnum).isEqualTo(ONE_PAIR)
    }

    @Test
    fun testPlayerAWinDrawGameBestRanking() {
        //Basic tests
        val game = GameUtil
        val playerA = Player(mockParty)
        val playerB = Player(mockParty)
        val playerList = mutableListOf<Player>(playerA, playerB)
        val dealer =mockParty
        val tableCards = mutableListOf<Card>()
        val deck: Deck = Deck(dealer)

        game.deal(playerList, tableCards, deck)
        game.callFlop(playerList, tableCards, deck)
        game.betRiver(playerList, tableCards, deck)
        game.betTurn(playerList, tableCards, deck)
        tableCards.clear()
        tableCards.add(Card(SPADES, CARD_10))
        tableCards.add(Card(SPADES, ACE))
        playerA.myCards = listOf(Card(CLUBS, CARD_10), Card(HEARTS, CARD_2))
        playerB.myCards = listOf(Card(CLUBS, CARD_2), Card(HEARTS, ACE))
        val winnerList = game.getWinner(playerList, tableCards)
        assertThat(winnerList.size).isEqualTo(1)
        assertThat(playerA).isEqualTo(winnerList.get(0))
        assertThat(playerA.rankingEnum).isEqualTo(ONE_PAIR)
        assertThat(playerB.rankingEnum).isEqualTo(ONE_PAIR)
    }
    @Test
    fun testPlayerBWinDrawGameHighCard() {
        //Basic tests
        val game = GameUtil
        val playerA = Player(mockParty)
        val playerB = Player(mockParty)
        val playerList = mutableListOf<Player>(playerA, playerB)
        val dealer =mockParty
        val tableCards = mutableListOf<Card>()
        val deck: Deck = Deck(dealer)

        game.deal(playerList, tableCards, deck)
        game.callFlop(playerList, tableCards, deck)
        game.betRiver(playerList, tableCards, deck)
        game.betTurn(playerList, tableCards, deck)
        tableCards.clear()

        tableCards.add(Card(SPADES, CARD_10))
        playerA.myCards = listOf(Card(CLUBS, CARD_10), Card(HEARTS, ACE))
        playerB.myCards = listOf(Card(CLUBS, CARD_2), Card(HEARTS, CARD_10))

        val winnerList = game.getWinner(playerList, tableCards)
        assertThat(winnerList.size).isEqualTo(1)
        assertThat(playerB).isEqualTo(winnerList.get(0))
        assertThat(playerA.rankingEnum).isEqualTo(ONE_PAIR)
        assertThat(playerB.rankingEnum).isEqualTo(ONE_PAIR)
    }
}