package com.poker.flows

import co.paralleluniverse.fibers.Suspendable
import com.poker.contracts.PokerContract
import com.poker.states.GameState
import com.poker.states.Player
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.minutes
import java.util.*
import net.corda.core.identity.CordaX500Name as CordaX500Name1

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class AddPlayerFlow(val gameID: String, val player: Party) : FlowLogic<UniqueIdentifier>() {
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
        object VALIDATING : ProgressTracker.Step("Performing initial steps - get game state, player and check if they are valid")
        object BUILDING : ProgressTracker.Step("Building and verifying transaction")
        object SIGNING : ProgressTracker.Step("Dealer Signing transaction.")
        object COLLECTING : ProgressTracker.Step("Collecting signatures from the player.") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }

        object FINALISING : ProgressTracker.Step("Finalising transaction. - Full Final signature on the vault") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(VALIDATING, BUILDING, SIGNING, COLLECTING, FINALISING)
    }

    @Suspendable
    override fun call(): UniqueIdentifier {
        // Step 1. Validation.
        progressTracker.currentStep = VALIDATING
        val qr = this.serviceHub.vaultService.queryBy(GameState::class.java)
        val gameStateRef = this.serviceHub.vaultService.queryBy(GameState::class.java, QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier(id = UUID.fromString(gameID))))).states.first()
        // val gameStateRef = this.serviceHub.vaultService.queryBy(GameState::class.java).states.filter{it.state.data.linearId == UniqueIdentifier(gameID)}.first()
        val gameState = gameStateRef.state.data
        val playerState: Player = Player(party = player, dealer = gameState.dealer)
        val notary = this.serviceHub.networkMapCache.notaryIdentities.first()

        // Step 2. Building.
        progressTracker.currentStep = BUILDING
        val newGameState = gameState.addPlayer(player)
        val currentParticipants = gameState.participants.map { it.owningKey } + player.owningKey
        val txCommand = Command(PokerContract.Commands.ADD_PLAYER(), currentParticipants)
        val txBuilder = TransactionBuilder(notary)
                .addInputState(gameStateRef)
                .addOutputState(newGameState)
                .addOutputState(playerState)
                .addCommand(txCommand)
                .setTimeWindow(serviceHub.clock.instant(), 5.minutes)
        txBuilder.verify(serviceHub)

        // Step 3. Sign the transaction.
        progressTracker.currentStep = SIGNING
        val dealerSignedTx = serviceHub.signInitialTransaction(txBuilder)

        // Step 4. Get the counter-party (Players) signature.
        progressTracker.currentStep = COLLECTING
        val otherPartySessions = newGameState.players.map { initiateFlow(it) }
        val fullySignedTx = subFlow(CollectSignaturesFlow(dealerSignedTx, otherPartySessions.toSet()))

        // Step 6. Finalise the transaction.
        progressTracker.currentStep = FINALISING
        val finalityFlow = subFlow(FinalityFlow(fullySignedTx, otherPartySessions.toSet()))
        return playerState.linearId
    }

}

@InitiatedBy(AddPlayerFlow::class)
class Acceptor(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {

            }
        }
        val txId = subFlow(signTransactionFlow).id

        return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
    }
}