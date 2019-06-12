package com.poker.states

import com.poker.contracts.PokerContract
import com.poker.model.Card
import com.poker.model.RoundEnum
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.LocalDateTime

@BelongsToContract(PokerContract::class)
@CordaSerializable
data class GameState(
        override val linearId: UniqueIdentifier,
        val dealer: Party,
        val players: List<Party>,
        val deckIdentifier: UniqueIdentifier,
        var tableCards: List<Card>,
        var rounds: RoundEnum,
        var betAmount: Int,
        var winner : Party? = null,
        val lastChange: LocalDateTime = LocalDateTime.now()
) : LinearState {

    override val participants: List<Party> get() = listOf(dealer) + players

    fun addBetAmount(amount: Int) = copy(
            betAmount = betAmount + amount,
            lastChange = LocalDateTime.now()
    )

    fun addPlayer(player: Party) = copy(
            players = players + player,
            lastChange = LocalDateTime.now()
    )
    //TODO: Deck Signature to be included in GameState . There should be a way for players to ensure that the deck is not tampered
    //TODO: lastchange to be updated on Rounds
}