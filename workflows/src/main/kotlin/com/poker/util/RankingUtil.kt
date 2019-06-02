package com.poker.util

import com.poker.model.Card
import com.poker.model.CardRankEnum
import com.poker.model.CardRankEnum.*
import com.poker.states.Player
import com.poker.model.RankingEnum
import com.poker.model.RankingEnum.*
import java.util.*

/*
ROYAL_FLUSH,
STRAIGHT_FLUSH,
FOUR_OF_A_KIND,
FULL_HOUSE,
FLUSH,
STRAIGHT,
THREE_OF_A_KIND,
TWO_PAIR,
ONE_PAIR,
HIGH_CARD
*/
object RankingUtil {

    fun getRankingToInt(player: Player): Int {
        return player.rankingEnum.ordinal
    }

    fun checkRanking(player: Player, tableCards: List<Card>) {

        //HIGH_CARD
        val highCard = getHighCard(player, tableCards)
        player.highCard = highCard

        //ROYAL_FLUSH
        var rankingList = getRoyalFlush(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, ROYAL_FLUSH, rankingList)
            return
        }
        //STRAIGHT_FLUSH
        rankingList = getStraightFlush(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, STRAIGHT_FLUSH,
                    rankingList)
            return
        }
        //FOUR_OF_A_KIND
        rankingList = getFourOfAKind(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, FOUR_OF_A_KIND,
                    rankingList)
            return
        }
        //FULL_HOUSE
        rankingList = getFullHouse(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, FULL_HOUSE, rankingList)
            return
        }
        //FLUSH
        rankingList = getFlush(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, FLUSH, rankingList)
            return
        }
        //STRAIGHT
        rankingList = getStraight(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, STRAIGHT, rankingList)
            return
        }
        //THREE_OF_A_KIND
        rankingList = getThreeOfAKind(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, THREE_OF_A_KIND,
                    rankingList)
            return
        }
        //TWO_PAIR
        rankingList = getTwoPair(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, TWO_PAIR, rankingList)
            return
        }
        //ONE_PAIR
        rankingList = getOnePair(player, tableCards)
        if (rankingList != null) {
            setRankingEnumAndList(player, ONE_PAIR, rankingList)
            return
        }
        //HIGH_CARD
        player.rankingEnum = HIGH_CARD
        val highCardRankingList = ArrayList<Card>()
        highCardRankingList.add(highCard)
        player.highCardRankingList = highCardRankingList
        return
    }

    fun getRoyalFlush(player: Player, tableCards: List<Card>): List<Card>? {
        if (!isSameSuit(player, tableCards)) {
            return null
        }

        val rankEnumList = toRankEnumList(player, tableCards)

        return if (rankEnumList.contains(CARD_10)
                && rankEnumList.contains(JACK)
                && rankEnumList.contains(QUEEN)
                && rankEnumList.contains(KING)
                && rankEnumList.contains(ACE)) {

            getMergedCardList(player, tableCards)
        } else null

    }

    fun getStraightFlush(player: Player,
                         tableCards: List<Card>): List<Card>? {
        return getSequence(player, tableCards, 5, true)
    }

    fun getFourOfAKind(player: Player,
                       tableCards: List<Card>): List<Card>? {
        val mergedList = getMergedCardList(player, tableCards)
        return checkPair(mergedList, 4)
    }

    fun getFullHouse(player: Player, tableCards: List<Card>): List<Card>? {
        val mergedList = getMergedCardList(player, tableCards)
        val threeList = checkPair(mergedList, 3)
        if (threeList != null) {
            mergedList.removeAll(threeList)
            val twoList = checkPair(mergedList, 2)
            if (twoList != null) {
                threeList.addAll(twoList)
                return threeList
            }
        }
        return null
    }

    fun getFlush(player: Player, tableCards: List<Card>): List<Card>? {
        val mergedList = getMergedCardList(player, tableCards)
        val flushList = ArrayList<Card>()

        for (card1 in mergedList) {
            for (card2 in mergedList) {
                if (card1.suit.equals(card2.suit)) {
                    if (!flushList.contains(card1)) {
                        flushList.add(card1)
                    }
                    if (!flushList.contains(card2)) {
                        flushList.add(card2)
                    }
                }
            }
            if (flushList.size == 5) {
                return flushList
            }
            flushList.clear()
        }
        return null
    }

    //S‹o 5 cartas seguidas de naipes diferentes, caso empate ganha aquele com a maior sequ?ncia.
    fun getStraight(player: Player, tableCards: List<Card>): List<Card>? {
        return getSequence(player, tableCards, 5, false)
    }

    fun getThreeOfAKind(player: Player,
                        tableCards: List<Card>): List<Card>? {
        val mergedList = getMergedCardList(player, tableCards)
        return checkPair(mergedList, 3)
    }

    fun getTwoPair(player: Player, tableCards: List<Card>): List<Card>? {
        val mergedList = getMergedCardList(player, tableCards)
        val twoPair1 = checkPair(mergedList, 2)
        if (twoPair1 != null) {
            mergedList.removeAll(twoPair1)
            val twoPair2 = checkPair(mergedList, 2)
            if (twoPair2 != null) {
                twoPair1.addAll(twoPair2)
                return twoPair1
            }
        }
        return null
    }

    fun getOnePair(player: Player, tableCards: List<Card>): List<Card>? {
        val mergedList = getMergedCardList(player, tableCards)
        return checkPair(mergedList, 2)
    }

    fun getHighCard(player: Player, tableCards: List<Card>): Card {
        val allCards = ArrayList<Card>()
        allCards.addAll(tableCards)
        allCards.add(player.myCards[0])
        allCards.add(player.myCards[1])

        var highCard = allCards[0]
        for (card in allCards) {
            if (card.getRankToInt() > highCard.getRankToInt()) {
                highCard = card
            }
        }
        return highCard
    }

    private fun getSequence(player: Player,
                            tableCards: List<Card>, sequenceSize: Int?, compareSuit: Boolean): List<Card>? {
        val orderedList = getOrderedCardList(player, tableCards)
        val sequenceList = ArrayList<Card>()

        var cardPrevious: Card? = null
        for (card in orderedList) {
            if (cardPrevious != null) {
                if (card.getRankToInt() - cardPrevious.getRankToInt() === 1) {
                    if ((!compareSuit) || cardPrevious.suit.equals(card.suit)) {
                        if (sequenceList.size == 0) {
                            sequenceList.add(cardPrevious)
                        }
                        sequenceList.add(card)
                    }
                } else {
                    if (sequenceList.size == sequenceSize) {
                        return sequenceList
                    }
                    sequenceList.clear()
                }
            }
            cardPrevious = card
        }

        return if (sequenceList.size == sequenceSize) sequenceList else null
    }

    private fun getMergedCardList(player: Player,
                                  tableCards: List<Card>): MutableList<Card> {
        val merged = ArrayList<Card>()
        merged.addAll(tableCards)
        merged.add(player.myCards[0])
        merged.add(player.myCards[1])
        return merged
    }

    private fun getOrderedCardList(player: Player,
                                   tableCards: List<Card>): List<Card> {
        val ordered: MutableList<Card> = getMergedCardList(player, tableCards)
        Collections.sort(ordered, Comparator<Card> { c1: Card, c2: Card -> if (c1.getRankToInt() < c2.getRankToInt()) -1 else 1 })
        return ordered
    }

    private fun checkPair(mergedList: List<Card>, pairSize: Int?): MutableList<Card>? {
        val checkedPair = ArrayList<Card>()
        for (card1 in mergedList) {
            checkedPair.add(card1)
            for (card2 in mergedList) {
                if (!card1.equals(card2) && card1.rank.equals(card2.rank)) {
                    checkedPair.add(card2)
                }
            }
            if (checkedPair.size == pairSize) {
                return checkedPair
            }
            checkedPair.clear()
        }
        return null
    }

    private fun isSameSuit(player: Player, tableCards: List<Card>): Boolean {
        val suit = player.myCards[0].suit

        if (!suit.equals(player.myCards[1].suit)) {
            return false
        }

        for (card in tableCards) {
            if (!card.suit.equals(suit)) {
                return false
            }
        }

        return true
    }

    private fun toRankEnumList(player: Player,
                               tableCards: List<Card>): List<CardRankEnum> {
        val rankEnumList = ArrayList<CardRankEnum>()

        for (card in tableCards) {
            rankEnumList.add(card.rank)
        }

        rankEnumList.add(player.myCards[0].rank)
        rankEnumList.add(player.myCards[1].rank)

        return rankEnumList
    }

    private fun setRankingEnumAndList(player: Player,
                                      rankingEnum: RankingEnum, rankingList: List<Card>) {
        player.rankingEnum = rankingEnum
        player.highCardRankingList = rankingList
    }
}
