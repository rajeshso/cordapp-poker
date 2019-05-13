package com.poker.model

data class Player (

        var cards: Array<Card?> = arrayOfNulls<Card>(2),

        var rankingEnum: RankingEnum? = null,

        var rankingList: List<Card>? = null,

        var highCard: Card? = null
        )