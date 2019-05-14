package com.poker.contracts

import com.poker.states.GameState
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

// ************
// * Contract *
// ************
class PokerContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        @JvmStatic
        val ID = PokerContract::class.qualifiedName!!
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        val setOfSigners = command.signers.toSet()
        when (command.value) {
            is Commands.StartGame -> verifyStartGame(tx, setOfSigners)
            is Commands.Deal -> verifyDeal(tx, setOfSigners)
            is Commands.Flop -> verifyDeal(tx, setOfSigners)
            is Commands.River -> verifyDeal(tx, setOfSigners)
            is Commands.Turn -> verifyDeal(tx, setOfSigners)
            is Commands.Winner -> verifyWinner(tx, setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised command.")
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class StartGame : TypeOnlyCommandData(), Commands
        class Deal : TypeOnlyCommandData(), Commands
        class Flop : TypeOnlyCommandData(), Commands
        class River : TypeOnlyCommandData(), Commands
        class Turn : TypeOnlyCommandData(), Commands
        class Winner : TypeOnlyCommandData(), Commands
    }

    private fun keysFromParticipants(gameState: GameState): Set<PublicKey> {
        return gameState.participants.map {
            it.owningKey
        }.toSet()
    }

    // This only allows one gamestate issuance per transaction.
    private fun verifyStartGame(tx: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
        "No inputs should be consumed when issuing an gamestate." using (tx.inputStates.isEmpty())
        "Only one gamestate state should be created when issuing an gamestate." using (tx.outputStates.size == 1)
        val gameState = tx.outputsOfType<GameState>().single()
//        "A newly issued gamestate must have a positive amount." using (gamestate. .amount.quantity > 0)

    }
    private fun verifyDeal(tx: LedgerTransaction, signers: Set<PublicKey>) : Unit {
        val gameState = tx.outputsOfType<GameState>().single()
        //        "The dealer and player cannot be the same identity." using (gamestate.borrower != gamestate.lender)
        "Both dealer and player together only may sign gamestate issue transaction." using
                (signers == keysFromParticipants(gameState))
        TODO("Yet to implement")
    }

    private fun verifyFlop(tx: LedgerTransaction, signers: Set<PublicKey>) : Unit {
        TODO("Yet to implement")
    }
    private fun verifyRiver(tx: LedgerTransaction, signers: Set<PublicKey>) : Unit {
        TODO("Yet to implement")
    }
    private fun verifyTurn(tx: LedgerTransaction, signers: Set<PublicKey>) : Unit {
        TODO("Yet to implement")
    }
    private fun verifyWinner(tx: LedgerTransaction, signers: Set<PublicKey>) : Unit {
        TODO("Yet to implement")
    }
}