package com.poker.flows

import co.paralleluniverse.fibers.Suspendable
import com.poker.contracts.PokerContract
import com.poker.model.RoundEnum
import com.poker.states.GameState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class StartGameFlow(val players: List<Party>, val notary: Party) : FlowLogic<UniqueIdentifier>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() : UniqueIdentifier {
        // Initiator flow logic goes here.
        val dealer = serviceHub.myInfo.legalIdentities.first()
        // Stage 1.
        val gameState: GameState = GameState(UniqueIdentifier(), dealer,players, emptyList(),RoundEnum.Started)
        val txCommand = Command(PokerContract.Commands.Start_GAME(), gameState.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary)
                .addOutputState(gameState)
                .addCommand(txCommand)

        // Stage 2
        txBuilder.verify(serviceHub)

        // Stage 3
        val dealerSignedTx = serviceHub.signInitialTransaction(txBuilder)

        //Stage 4
        val otherPartySessions = players.map { initiateFlow(it) }
        val fullySignedTx = subFlow(CollectSignaturesFlow(dealerSignedTx, otherPartySessions.toSet()))

        // Step 5
        val finalityFlow = subFlow(FinalityFlow(fullySignedTx, otherPartySessions.toSet()))
        return gameState.linearId
    }
}

@InitiatedBy(StartGameFlow::class)
class AcceptStartGame(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be an Game Start transaction." using (output is GameState)
                val gameState = output as GameState
               // "I won't accept IOUs with a value over 100." using (gameState.value <= 100)
            }
        }
        val txId = subFlow(signTransactionFlow).id

        return subFlow(ReceiveFinalityFlow(counterpartySession, expectedTxId = txId))
    }
}
