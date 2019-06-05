package com.poker.util

import com.poker.model.Card
import com.poker.states.Deck
import com.poker.states.Player
import java.util.*

object GameUtil {

    fun deal(players: MutableList<Player>, tableCards: MutableList<Card>, deck: Deck) {
        for (player in players) {
            player.myCards = listOf(deck.pop(), deck.pop())
        }
        checkPlayersRanking(players, tableCards)
    }

    fun betTurn(players: MutableList<Player>, tableCards: MutableList<Card>, deck: Deck) {
        //deck.pop()
        tableCards.add(deck.pop())
        checkPlayersRanking(players, tableCards)
    }

    fun betRiver(players: MutableList<Player>, tableCards: MutableList<Card>, deck: Deck) {
        //deck.pop()
        tableCards.add(deck.pop())
        checkPlayersRanking(players, tableCards)
    }

    /**
     * double initial bet
     */
    fun callFlop(players: MutableList<Player>, tableCards: MutableList<Card>, deck: Deck) {
        //deck.pop()
        tableCards.add(deck.pop())
        tableCards.add(deck.pop())
        tableCards.add(deck.pop())
        checkPlayersRanking(players, tableCards)
    }

    fun getWinner(players: MutableList<Player>, tableCards: MutableList<Card>): List<Player> {
        checkPlayersRanking(players, tableCards)
        val winnerList = ArrayList<Player>()
        var winner = players.get(0)
        var winnerRank = RankingUtil.getRankingToInt(winner)
        winnerList.add(winner)
        for (i in 1 until players.size) {
            val player = players.get(i)
            val playerRank = RankingUtil.getRankingToInt(player)
            //Draw game
            if (winnerRank === playerRank) {
                var highHandPlayer = checkHighSequence(winner, player)
                //Draw checkHighSequence
                if (highHandPlayer == null) {
                    highHandPlayer = checkHighCardWinner(winner, player)
                }
                //Not draw in checkHighSequence or checkHighCardWinner
                if (highHandPlayer != null && !winner.equals(highHandPlayer)) {
                    winner = highHandPlayer
                    winnerList.clear()
                    winnerList.add(winner)
                } else if (highHandPlayer == null) {
                    //Draw in checkHighSequence and checkHighCardWinner
                    winnerList.add(winner)
                }
            } else if (winnerRank < playerRank) {
                winner = player
                winnerList.clear()
                winnerList.add(winner)
            }
            winnerRank = RankingUtil.getRankingToInt(winner)
        }

        return winnerList
    }

    private fun checkHighSequence(player1: Player, player2: Player): Player? {
        val player1Rank = sumRankingList(player1)
        val player2Rank = sumRankingList(player2)
        if (player1Rank > player2Rank) {
            return player1
        } else if (player1Rank < player2Rank) {
            return player2
        }
        return null
    }

    private fun checkHighCardWinner(player1: Player, player2: Player): Player? {
        var winner = compareHighCard(player1, player1.highCard!!,
                player2, player2.highCard!!)
        if (winner == null) {
            var player1Card = RankingUtil.getHighCard(player1,
                    emptyList<Card>())
            var player2Card = RankingUtil.getHighCard(player2,
                    emptyList<Card>())
            winner = compareHighCard(player1, player1Card, player2, player2Card)
            if (winner != null) {
                player1.highCard = player1Card
                player2.highCard = player2Card
            } else if (winner == null) {
                player1Card = getSecondHighCard(player1, player1Card)
                player2Card = getSecondHighCard(player2, player2Card)
                winner = compareHighCard(player1, player1Card, player2,
                        player2Card)
                if (winner != null) {
                    player1.highCard = player1Card
                    player2.highCard = player2Card
                }
            }
        }
        return winner
    }

    private fun checkPlayersRanking(players: MutableList<Player>, tableCards: MutableList<Card>) {
        for (player in players) {
            RankingUtil.checkRanking(player, tableCards)
        }
    }

    /*
	 * TODO This method must be moved to RankingUtil
	 */
    private fun sumRankingList(player: Player): Int {
        var sum: Int = 0
        for (card in player.highCardRankingList) {
            sum += card.getRankToInt()
        }
        return sum
    }

    /*
	 * TODO This method must be moved to RankingUtil
	 */
    private fun getSecondHighCard(player: Player, card: Card): Card {
        return if (player.myCards[0].equals(card)) {
            player.myCards[1]
        } else player.myCards[0]
    }

    private fun compareHighCard(player1: Player, player1HighCard: Card,
                                player2: Player, player2HighCard: Card): Player? {
        if (player1HighCard.getRankToInt() > player2HighCard.getRankToInt()) {
            return player1
        } else if (player1HighCard.getRankToInt() < player2HighCard
                        .getRankToInt()) {
            return player2
        }
        return null
    }


}