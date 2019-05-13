package com.poker.states

import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

interface OwnerState : ContractState {
    val dealer: AbstractParty
}