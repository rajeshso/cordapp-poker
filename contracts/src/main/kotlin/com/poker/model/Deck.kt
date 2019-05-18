package com.poker.model

import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class Deck(_shuffledCards: List<Card>) {
    private val cards = _shuffledCards
    private var index = 0
    val signature = cards.joinToString(",")

    fun pop(): Card {
        return if (index < cards.size) {
            cards.get(index++)
        } else {
            throw NoSuchElementException("Deck ran out of cards")
        }
    }

    companion object {
        private val allCards: HashSet<Card>

        init {
            allCards = HashSet<Card>()
            for (suit in CardSuitEnum.values()) {
                for (rank in CardRankEnum.values()) {
                    allCards.add(Card(suit, rank))
                }
            }
        }

        fun newSheffledDeck(): Deck {
            // Created a new Deck of Shuffled Cards
            val cards = ArrayList<Card>(52)
            cards.addAll(allCards)
            Collections.shuffle(cards, Random())
            return Deck(cards)
        }
    }
}