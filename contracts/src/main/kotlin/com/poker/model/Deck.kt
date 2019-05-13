package com.poker.model

import java.util.*

//TODO: Should this be a Singleton ? This means only one Game can be organized by the Dealer
object Deck {
    private var cards: MutableList<Card> = mutableListOf<Card>()
    private var random: Random = Random()

    init {
        cards = ArrayList<Card>()
        for (suit in CardSuitEnum.values()) {
            for (rank in CardRankEnum.values()) {
                cards.add(Card(suit, rank))
            }
        }
    }

    fun pop(): Card {
        return cards.removeAt(random.nextInt(cards.size))
    }
}