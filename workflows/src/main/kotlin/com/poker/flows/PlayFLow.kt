package com.poker.flows

import co.paralleluniverse.fibers.Suspendable
import com.poker.contracts.PokerContract
import com.poker.model.Card
import com.poker.model.RoundEnum
import com.poker.model.RoundEnum.Dealt
import com.poker.model.RoundEnum.Flopped
import com.poker.model.RoundEnum.Rivered
import com.poker.model.RoundEnum.Turned
import com.poker.model.RoundEnum.Winner
import com.poker.states.Deck
import com.poker.states.GameState
import com.poker.states.Player
import com.poker.util.GameUtil
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

// *********
// * Flows *
// * Dealer initiates the flow
// * Dealer constructs the flow with game id and action
// *  the permitted actions are deal, flop, river, turn, evaluate winner and close
// *********
@InitiatingFlow
@StartableByRPC
class PlayFLow(val gameID: String, val round: String) : FlowLogic<Unit>() {
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
        object VALIDATING : ProgressTracker.Step("Performing initial steps - get game state and round enum player and check if they are valid")
        object BUILDING : ProgressTracker.Step("Building and verifying transaction. Do the Play and update the cards")
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
    override fun call() {
        // Initiator flow logic goes here.
        // Step 1. Validation.
        progressTracker.currentStep = VALIDATING
        val qr = this.serviceHub.vaultService.queryBy(GameState::class.java)
        val oldGameStateRef = this.serviceHub.vaultService.queryBy(GameState::class.java, QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier(id = UUID.fromString(gameID))))).states.first()
        val oldGameState: GameState = oldGameStateRef.state.data
        val players = oldGameState.players
        val notary = this.serviceHub.networkMapCache.notaryIdentities.first()
        val roundEnum = RoundEnum.valueOf(round)
        val oldDeckStateRef = this.serviceHub.vaultService.queryBy(Deck::class.java, QueryCriteria.LinearStateQueryCriteria(linearId = listOf(oldGameState.deckIdentifier))).states.first()
        val newDeckState = oldDeckStateRef.state.data.copy()
        var newGameState = oldGameState.copy(rounds = roundEnum)
        val newPlayerStates = mutableListOf<Player>()
        var txBuilder = TransactionBuilder(notary)

        // Step 2. Building.
        progressTracker.currentStep = BUILDING
        //Create a new copy of Player state for every player state
        for (playerParty in players) {
            val oldPlayerState = this.serviceHub.vaultService.queryBy(Player::class.java).states.filter { it.state.data.party == playerParty }.first().state.data
            newPlayerStates.add(oldPlayerState.copy())
        }
        when (roundEnum) {
            Dealt -> {
                GameUtil.deal(newPlayerStates, mutableListOf<Card>(), newDeckState)
                txBuilder = txBuilder.addCommand(Command(PokerContract.Commands.DEALT(), newGameState.participants.map { it.owningKey }))
            }
            Flopped -> {
                val tableCards = newGameState.tableCards.toMutableList()
                GameUtil.callFlop(newPlayerStates, tableCards, newDeckState)
                newGameState = newGameState.copy(tableCards = tableCards.toList())
                txBuilder = txBuilder.addCommand(Command(PokerContract.Commands.FLOPPED(), newGameState.participants.map { it.owningKey }))
            }
            Rivered -> {
                val tableCards = newGameState.tableCards.toMutableList()
                GameUtil.betRiver(newPlayerStates, tableCards, newDeckState)
                newGameState = newGameState.copy(tableCards = tableCards.toList())
                txBuilder = txBuilder.addCommand(Command(PokerContract.Commands.RIVERED(), newGameState.participants.map { it.owningKey }))
            }
            Turned -> {
                val tableCards = newGameState.tableCards.toMutableList()
                GameUtil.betTurn(newPlayerStates, tableCards, newDeckState)
                newGameState = newGameState.copy(tableCards = tableCards.toList())
                txBuilder = txBuilder.addCommand(Command(PokerContract.Commands.TURNED(), newGameState.participants.map { it.owningKey }))
            }
            Winner -> {
                val tableCards = newGameState.tableCards.toMutableList()
                val winnerList = GameUtil.getWinner(newPlayerStates, tableCards)
                println("Winner List is " + winnerList + " and the winning amount of "+newGameState.betAmount + " goes to "+ winnerList.get(0))
                txBuilder = txBuilder.addCommand(Command(PokerContract.Commands.WINNER(), newGameState.participants.map { it.owningKey }))
            }
            else ->
                throw RuntimeException("This should not happen")

        }
        txBuilder = txBuilder
                .addInputState(oldGameStateRef)
                .addInputState(oldDeckStateRef)
        for (playerParty in newPlayerStates) {
            val oldPlayerStateRef = this.serviceHub.vaultService.queryBy(Player::class.java, QueryCriteria.LinearStateQueryCriteria(linearId = listOf(playerParty.linearId))).states.first()
            txBuilder = txBuilder.addInputState(oldPlayerStateRef)
        }
        for (newPlayerState in newPlayerStates) {
            txBuilder = txBuilder.addOutputState(newPlayerState)
        }
        txBuilder = txBuilder.addOutputState(newGameState)
                .addOutputState(newDeckState)
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
    }

}

@InitiatedBy(PlayFLow::class)
class PlayFlowResponder(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {
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