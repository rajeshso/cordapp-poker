package com.poker.states

import com.poker.contracts.DeckContract
import com.poker.model.Card
import com.poker.model.CardRankEnum
import com.poker.model.CardSuitEnum
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.CommandAndState
import net.corda.core.contracts.OwnableState
import net.corda.core.identity.AbstractParty
import net.corda.core.serialization.CordaSerializable
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@BelongsToContract(DeckContract::class)
@CordaSerializable
data class Deck(override val owner: AbstractParty) : OwnableState {

    override val participants: List<AbstractParty> = listOf(owner)

    var index = 0
    val signature get() =  cards.joinToString(",")

    fun pop(): Card {
        return if (index < cards.size) {
            cards.get(index++)
        } else {
            throw NoSuchElementException("Deck ran out of cards")
        }
    }

    companion object {
        val cards: List<Card>

        init {
            val allCards: HashSet<Card> = HashSet<Card>()
            for (suit in CardSuitEnum.values()) {
                for (rank in CardRankEnum.values()) {
                    allCards.add(Card(suit, rank))
                }
            }
            // Created a new Deck of Shuffled Cards
            cards = ArrayList<Card>(52)
            cards.addAll(allCards)
            Collections.shuffle(cards, Random())
        }
    }
    override fun withNewOwner(newOwner: AbstractParty): CommandAndState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}