package com.poker.states

import com.poker.contracts.PokerContract
import com.poker.model.Card
import com.poker.model.CardRankEnum
import com.poker.model.CardSuitEnum
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.CommandAndState
import net.corda.core.contracts.OwnableState
import net.corda.core.identity.AbstractParty

@BelongsToContract(PokerContract::class)
data class GameState(val player_issuedCards: Map<String, String>,
                     val notIssuedCards: Set<String>,
                     override val participants: List<AbstractParty> = listOf(), override val owner: AbstractParty) : OwnableState {
    override fun withNewOwner(newOwner: AbstractParty): CommandAndState {
        TODO("not implemented")
        val c:Card = Card(CardSuitEnum.ACE, CardRankEnum.ACE)
    }
}
