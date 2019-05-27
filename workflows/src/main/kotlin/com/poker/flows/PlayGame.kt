package com.poker.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.utilities.ProgressTracker

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class PlayGameFlow : FlowLogic<Unit>() {
    /**
     * Tracks progress throughout the flows call execution.
     */
    override val progressTracker: ProgressTracker
        get() {
            return ProgressTracker(
                    INITIALISING,
                    BUILDING,
                    SIGNING,
                    COLLECTING,
                    FINALISING
            )
        }

    companion object {
        object INITIALISING : ProgressTracker.Step("Performing initial steps - get deck and game state and check if the play is valid")
        object BUILDING : ProgressTracker.Step("Building and verifying transaction - Play")
        object SIGNING : ProgressTracker.Step("Dealer Signing transaction.")
        object COLLECTING : ProgressTracker.Step("Collecting signatures from the players.") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }
        object FINALISING : ProgressTracker.Step("Finalising transaction. - Full Final signature on the vault") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(INITIALISING, BUILDING, SIGNING, COLLECTING, FINALISING)
    }

    @Suspendable
    override fun call() {
        // PlayGameFlow flow logic goes here.
    }

    //deal means issue two cards to each player
    //flop means add three cards to tableCards
    //betTurn means add one card to the tableCards
    //betRiver means add one card to the tableCards


}

@InitiatedBy(PlayGameFlow::class)
class RespondPlayGame(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // RespondPlayGame flow logic goes here.
    }
}