package com.poker.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************
class PlayContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
//        const val ID = "com.poker.contracts.DeckContract"
        @JvmStatic
        val ID = PlayContract::class.qualifiedName!!
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Deal : Commands
        class Flop : Commands
        class BetTurn : Commands
        class BetRiver : Commands
    }
}