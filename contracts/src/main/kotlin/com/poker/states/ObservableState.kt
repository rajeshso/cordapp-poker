package com.poker.states

import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

interface ObservableState : ContractState {
    val observers: List<AbstractParty>
}