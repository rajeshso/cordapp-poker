package com.poker.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class CardSuitEnum {
    Diamonds,
    Hearts,
    Spades,
    Clubs,
}