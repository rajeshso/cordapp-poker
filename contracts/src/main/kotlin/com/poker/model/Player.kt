package com.poker.model

import net.corda.core.identity.AbstractParty

data class Player(
        val party: AbstractParty
) {
    var myCards: List<Card> = emptyList<Card>()
    var rankingEnum: RankingEnum = RankingEnum.HIGH_CARD
    var highCard: Card? = null
    var highCardRankingList: List<Card> = emptyList<Card>()

}