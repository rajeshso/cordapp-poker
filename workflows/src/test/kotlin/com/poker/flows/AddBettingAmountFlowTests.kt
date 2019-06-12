package com.poker.flows

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.poker.model.RoundEnum
import com.poker.states.GameState
import com.poker.states.PlayerState
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test

class AddBettingAmountFlowTests {
    companion object {
        val log = loggerFor<AddBettingAmountFlowTests>()
    }

    private val network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
            TestCordapp.findCordapp("com.poker.contracts"),
            TestCordapp.findCordapp("com.poker.flows")
    )))
    private val dealer = network.createNode()
    private val playerA = network.createNode()
    private val playerB = network.createNode()

    init {
        listOf(playerA, playerB, dealer).forEach {
            it.registerInitiatedFlow(AddPlayerAcceptor::class.java)
            it.registerInitiatedFlow(PlayFlowResponder::class.java)
            it.registerInitiatedFlow(AcceptBettingAmountFlow::class.java)
        }
    }

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `Add a Betting amount should add the value in game state`() {
        val betAmount = 100
        val notaryNode = network.defaultNotaryNode.info.legalIdentities.first()
        val startGameFlow = dealer.startFlow(StartGameFlow(notaryNode)).toCompletableFuture()
        network.runNetwork()
        val gameUID = startGameFlow.getOrThrow()

        dealer.startFlow(AddPlayerFlow(gameUID.id.toString(), playerA.info.legalIdentities.first())).toCompletableFuture()
        network.runNetwork()
        dealer.startFlow(AddPlayerFlow(gameUID.id.toString(), playerB.info.legalIdentities.first())).toCompletableFuture()
        network.runNetwork()
        dealer.startFlow(PlayFLow(gameUID.toString(), RoundEnum.Dealt.name)).toCompletableFuture()
        network.runNetwork()
        playerA.startFlow(AddBettingAmountFlow(gameUID.toString(), betAmount)).toCompletableFuture()
        network.runNetwork()

        val dealerAVault = dealer.services.vaultService.queryBy<GameState>()
        val dealerStateAndRef = dealerAVault.states.first()
        val gameState = dealerStateAndRef.state.data
        assertThat ( gameState.betAmount ).isEqualTo(betAmount)
    }
}