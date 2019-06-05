package com.poker.states

import com.poker.contracts.PokerContract
import com.poker.model.Card
import com.poker.model.RoundEnum
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
        var tableCards: List<Card>,
        var rounds: RoundEnum,
        val lastChange: LocalDateTime = LocalDateTime.now()
) : LinearState {

    override val participants: List<AbstractParty> get() = listOf(dealer) + players

    fun addTableCards(newTableCards: MutableList<Card>) = copy(
            tableCards = tableCards + newTableCards,
            lastChange = LocalDateTime.now()
    )

    fun addPlayer(player: Party) = copy(
            players = players + player,
            lastChange = LocalDateTime.now()
    )
}


