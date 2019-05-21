package com.poker

import com.poker.flows.AcceptStartGame
import com.poker.flows.StartGameFlow
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test

class FlowTests {
    companion object {
        val log = loggerFor<FlowTests>()
    }
    private val network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
            TestCordapp.findCordapp("com.poker.contracts"),
            TestCordapp.findCordapp("com.poker.flows")
    )))
    private val notary = network.createNode()
    private val dealer = network.createNode()
    private val playerA = network.createNode()
    private val playerB = network.createNode()

    init {
        listOf(playerA, playerB).forEach {
            it.registerInitiatedFlow(AcceptStartGame::class.java)
        }
    }

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `dummy test`() {
        val notaryNode = network.defaultNotaryNode.info.legalIdentities.first()
        val flow = dealer.startFlow(StartGameFlow(listOf(
                playerA.info.legalIdentities.first(),
                playerB.info.legalIdentities.first()
        ), notaryNode)).toCompletableFuture()
        network.runNetwork()
        val uid = flow.getOrThrow()
        log.info("game id: $uid")
    }
}