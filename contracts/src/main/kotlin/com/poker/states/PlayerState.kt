package com.poker.states

import com.poker.contracts.PokerContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(PokerContract::class)
data class PlayerState(val data: Set<String>, override val participants: List<AbstractParty> = listOf()) : ContractState
