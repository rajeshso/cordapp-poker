package com.poker.flows

import co.paralleluniverse.fibers.Suspendable
import com.poker.contracts.PokerContract
import com.poker.states.GameState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*
import net.corda.core.identity.CordaX500Name as CordaX500Name1

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class AddBettingAmountFlow(val gameID: String, val amount: Int) : FlowLogic<Unit>() {
    /**
     * Tracks progress throughout the flows call execution.
     */
    override val progressTracker: ProgressTracker
        get() {
            return ProgressTracker(
                    VALIDATING,
                    BUILDING,
                    SIGNING,
                    COLLECTING,
                    FINALISING
            )
        }

    companion object {
        object VALIDATING : ProgressTracker.Step("Performing initial steps - get game state and check if they are valid")
        object BUILDING : ProgressTracker.Step("Building and verifying transaction")
        object SIGNING : ProgressTracker.Step("Dealer Signing transaction.")
        object COLLECTING : ProgressTracker.Step("Collecting signatures from the dealer and other players.") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }
        object FINALISING : ProgressTracker.Step("Finalising transaction. - Full Final signature on the vault") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(VALIDATING, BUILDING, SIGNING, COLLECTING, FINALISING)
    }

    @Suspendable
    override fun call(): Unit {
        // Step 1. Validation.
        progressTracker.currentStep = VALIDATING
        val qr = this.serviceHub.vaultService.queryBy(GameState::class.java)
        val gameStateRef = this.serviceHub.vaultService.queryBy(GameState::class.java, QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier(id = UUID.fromString(gameID))))).states.first()
        val gameState = gameStateRef.state.data
        val notary = this.serviceHub.networkMapCache.notaryIdentities.first()

        // Step 2. Building.
        progressTracker.currentStep = BUILDING
        val newGameState = gameState.addBetAmount(amount)
        val currentParticipants = gameState.participants.map { it.owningKey }
        val txCommand = Command(PokerContract.Commands.BET(), currentParticipants)
        val txBuilder = TransactionBuilder(notary)
                .addInputState(gameStateRef)
                .addOutputState(newGameState)
                .addCommand(txCommand)
        txBuilder.verify(serviceHub)

        // Step 3. Sign the transaction.
        progressTracker.currentStep = SIGNING
        val playerSignedTx = serviceHub.signInitialTransaction(txBuilder)

        // Step 4. Get the counter-party (Players) signature.
        progressTracker.currentStep = COLLECTING
        val me = this.serviceHub.myInfo.legalIdentities.first()
        val participants = newGameState.participants - me
        val otherPartySessions = participants.map { initiateFlow(it) }
        val fullySignedTx = subFlow(CollectSignaturesFlow(playerSignedTx, otherPartySessions.toSet()))

        // Step 6. Finalise the transaction.
        progressTracker.currentStep = FINALISING
        val finalityFlow = subFlow(FinalityFlow(fullySignedTx, otherPartySessions.toSet()))
    }

}

@InitiatedBy(AddBettingAmountFlow::class)
class AcceptBettingAmountFlow(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                //TODO
            }
        }
        val txId = subFlow(signTransactionFlow).id

        return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
    }
}