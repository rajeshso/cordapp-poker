package com.poker.flows

import co.paralleluniverse.fibers.Suspendable
import com.poker.contracts.PokerContract
import com.poker.model.RoundEnum
import com.poker.states.Deck
import com.poker.states.GameState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.core.utilities.minutes

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class StartGameFlow(val players: List<Party>, val notary: Party) : FlowLogic<UniqueIdentifier>() {
    /**
     * Tracks progress throughout the flows call execution.
     */
    override val progressTracker: ProgressTracker
        get() {
            return ProgressTracker(
                    INITIALISING,
                    BUILDING,
                    DECKING,
                    SIGNING,
                    COLLECTING,
                    FINALISING
            )
        }

    companion object {
        object INITIALISING : Step("Performing initial steps - create game state.")
        object BUILDING : Step("Building and verifying transaction - Start Game tx with gamestate as output and no input")
        object DECKING : Step("Build Deck and store in the Dealer's vault")
        object SIGNING : Step("Dealer Signing transaction.")
        object COLLECTING : Step("Collecting signatures from the players.") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }

        object FINALISING : Step("Finalising transaction. - Full Final signature on the vault") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(INITIALISING, BUILDING, DECKING, SIGNING, COLLECTING, FINALISING)
    }

    @Suspendable
    override fun call(): UniqueIdentifier {
        // Step 1. Initialisation.
        progressTracker.currentStep = INITIALISING
        val dealer = serviceHub.myInfo.legalIdentities.first()
        val gameState: GameState = GameState(UniqueIdentifier(), dealer, players, emptyList(), RoundEnum.Started)

        // Step 2. Building.
        progressTracker.currentStep = BUILDING
        val txCommand = Command(PokerContract.Commands.Start_GAME(), gameState.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary)
                .addOutputState(gameState)
                .addCommand(txCommand)
                .setTimeWindow(serviceHub.clock.instant(), 5.minutes)
        txBuilder.verify(serviceHub)

        // Step 3. Decking.
        progressTracker.currentStep = DECKING
        val deck: Deck = Deck(dealer)
        val txInternalCommand = Command(PokerContract.Commands.Start_GAME(), gameState.participants.map { it.owningKey })
        val txInternalBuilder = TransactionBuilder(notary)
                .addOutputState(deck)
                .addCommand(txInternalCommand)
                .setTimeWindow(serviceHub.clock.instant(), 5.minutes)
        txInternalBuilder.verify(serviceHub)
        val dealerSignedTxForDecking = serviceHub.signInitialTransaction(txInternalBuilder)
        serviceHub.recordTransactions(dealerSignedTxForDecking)

        // Step 4. Sign the transaction.
        progressTracker.currentStep = SIGNING
        val dealerSignedTx = serviceHub.signInitialTransaction(txBuilder)

        // Step 5. Get the counter-party (Players) signature.
        progressTracker.currentStep = COLLECTING
        val otherPartySessions = players.map { initiateFlow(it) }
        val fullySignedTx = subFlow(CollectSignaturesFlow(dealerSignedTx, otherPartySessions.toSet()))

        // Step 6. Finalise the transaction.
        progressTracker.currentStep = FINALISING
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
