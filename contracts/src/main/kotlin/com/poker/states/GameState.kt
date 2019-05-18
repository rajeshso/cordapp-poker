package com.poker.states

import com.poker.contracts.PokerContract
import com.poker.model.*
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import net.corda.core.serialization.CordaSerializable
import java.time.LocalDateTime

@BelongsToContract(PokerContract::class)
@CordaSerializable
data class GameState(
        override val linearId: UniqueIdentifier,
        override val dealer: AbstractParty,
       // var deck: Deck = Deck(), // TODO Move the deck outside the Game State or hide the deck to players
        var players: MutableList<Player>,
        var tableCards: List<Card>,
        val lastChange: LocalDateTime = LocalDateTime.now()
        ) :  LinearState, OwnerState, ObservableState, QueryableState {

    override val participants: List<AbstractParty> get() = listOfNotNull(dealer) + players.map {it.party}

    override val observers: List<AbstractParty>
        get() = players.map {it.party}

    fun addTableCards( newTableCards: MutableList<Card>) = copy(
            tableCards = tableCards + newTableCards,
            lastChange = LocalDateTime.now()
            )

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}


