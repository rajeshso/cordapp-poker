package com.poker.states

import com.poker.contracts.PokerContract
import com.poker.model.Card
import com.poker.model.Player
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
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
        val playerA: AbstractParty,
        val playerB: AbstractParty,
        var tableCards: List<Card>,
        val lastChange: LocalDateTime = LocalDateTime.now()
) : LinearState, OwnerState, ObservableState, QueryableState {

    override val participants: List<AbstractParty> get() = listOf(dealer, playerA, playerB)

    override val observers: List<AbstractParty>
        get() = listOf(playerA, playerB)

    fun addTableCards(newTableCards: MutableList<Card>) = copy(
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


