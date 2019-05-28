package com.poker.flows

import com.poker.model.RoundEnum
import com.poker.states.GameState
import net.corda.core.node.services.queryBy
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StartGameFlowTests {
    companion object {
        val log = loggerFor<StartGameFlowTests>()
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
    fun `Starting the game should return a UID and all its state values are initialized`() {
        val notaryNode = network.defaultNotaryNode.info.legalIdentities.first()
        val flow = dealer.startFlow(StartGameFlow(listOf(
                playerA.info.legalIdentities.first(),
                playerB.info.legalIdentities.first()
        ), notaryNode)).toCompletableFuture()
        network.runNetwork()
        val uid = flow.getOrThrow()
        log.info("game id: $uid")
        assertNotNull(uid.id)
        val vault = dealer.services.vaultService.queryBy<GameState>()
        assertTrue(vault.states.size==1)
        val stateAndRef = vault.states.first()
        assertTrue(stateAndRef.state.notary == notaryNode)
        val gameState = stateAndRef.state.data
        assertTrue(gameState.players == listOf(playerA.info.legalIdentities.first(), playerB.info.legalIdentities.first()))
        assertTrue(gameState.rounds == RoundEnum.Started)
        assertTrue(gameState.tableCards.isEmpty())
    }
}