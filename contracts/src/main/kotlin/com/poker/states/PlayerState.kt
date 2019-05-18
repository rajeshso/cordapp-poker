package com.poker.states

import com.poker.contracts.PokerContract
import com.poker.model.Card
import com.poker.model.RankingEnum
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

// *********
// * State *
// *********
@BelongsToContract(PokerContract::class)
data class PlayerState(override val participants: List<AbstractParty> = listOf(),
                       override val linearId: UniqueIdentifier,
                       var cards: Array<Card?> = arrayOfNulls<Card>(2),
                       var rankingEnum: RankingEnum? = null,
                       var rankingList: List<Card>? = null,
                       var highCard: Card? = null
) : LinearState, QueryableState {

    fun deal(initCards: Array<Card?>) = copy(
            cards = initCards
    )

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


