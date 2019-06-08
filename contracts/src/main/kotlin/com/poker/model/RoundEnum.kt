package com.poker.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class RoundEnum {
    Started,
    Dealt,
    Flopped,
    Rivered,
    Turned,
    Winner
}