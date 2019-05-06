package com.poker.states

import com.poker.contracts.PokerContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.CommandAndState
import net.corda.core.contracts.OwnableState
import net.corda.core.identity.AbstractParty

@BelongsToContract(PokerContract::class)
data class DealerState(val player_issuedCards: Map<String, String>,
                       val notIssuedCards: Set<String>,
                       override val participants: List<AbstractParty> = listOf(), override val owner: AbstractParty) : OwnableState {
    override fun withNewOwner(newOwner: AbstractParty): CommandAndState {
        TODO("not implemented")
    }
}
