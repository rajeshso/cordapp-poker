<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# CorDapp Poker - Texas-Hold-Em

[![Build Status](https://travis-ci.org/rajeshso/cordapp-poker.svg?branch=cordapp-poker)](https://travis-ci.org/rajeshso/cordapp-poker)


Welcome to the Texas-Hold-Em-Process in CordaApp. 

# Pre-Requisites

See https://docs.corda.net/getting-set-up.html

See https://www.seilevel.com/requirements/wp-content/uploads/2014/07/Texas-Hold-Em-Process-Flow.png

# Usage

## Running tests inside IntelliJ

We recommend editing your IntelliJ preferences so that you use the Gradle runner - this means that the quasar utils
plugin will make sure that some flags (like ``-javaagent`` - see below) are
set for you.

To switch to using the Gradle runner:

* Navigate to ``Build, Execution, Deployment -> Build Tools -> Gradle -> Runner`` (or search for `runner`)
  * Windows: this is in "Settings"
  * MacOS: this is in "Preferences"
* Set "Delegate IDE build/run actions to gradle" to true
* Set "Run test using:" to "Gradle Test Runner"

If you would prefer to use the built in IntelliJ JUnit test runner, you can run ``gradlew installQuasar`` which will
copy your quasar JAR file to the lib directory. You will then need to specify ``-javaagent:lib/quasar.jar``
and set the run directory to the project root directory for each test.

## Building the nodes
./build.sh would build the project with the four nodes:
Notary
Dealer
Player A
Player B

If you are looking to run only unit tests, you may run the script :
./test.sh

## Running the nodes

./run.sh would bring up four nodes in different terminal windows.
You will notice the terminal title as Notary, Dealer and the two players

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Interacting with the nodes

### Shell

When started via the command line, each node will display an interactive shell:

    Welcome to the Corda interactive shell.
    Useful commands include 'help' to see what is available, and 'bye' to shut down the node.
    
    Tue Nov 06 11:58:13 GMT 2018>>>

You can use this shell to interact with your node. For example, enter `run networkMapSnapshot` to see a list of 
the other nodes on the network:

    Tue Nov 06 11:58:13 GMT 2018>>> run networkMapSnapshot
    [
    node {
        name "O=Notary,L=London,C=GB"
        notary = [validating : false]
        p2pPort 10002
        rpcSettings {
            address("localhost:10003")
            adminAddress("localhost:10043")
        }
    }
    node {
        name "O=PlayerA,L=London,C=GB"
        p2pPort 10005
        rpcSettings {
            address("localhost:10006")
            adminAddress("localhost:10046")
        }
        rpcUsers = [[ user: "user1", "password": "test", "permissions": ["ALL"]]]
    }
    node {
        name "O=PlayerB,L=New York,C=US"
        p2pPort 10008
        rpcSettings {
            address("localhost:10009")
            adminAddress("localhost:10047")
        }
        rpcUsers = [[ user: "user1", "password": "test", "permissions": ["ALL"]]]
    }
    node {
        name "O=Dealer,L=New York,C=US"
        p2pPort 10011
        rpcSettings {
            address("localhost:10010")
            adminAddress("localhost:10045")
        }
        rpcUsers = [[ user: "user1", "password": "test", "permissions": ["ALL"]]]
    }
    ]
    
    Tue Nov 06 12:30:11 GMT 2018>>> 

You can find out more about the node shell [here](https://docs.corda.net/shell.html).

You can run a Poker game on the nodes:

On the Dealer node, run the following to start the game:
`flow start com.poker.flows.StartGameFlow notary: "O=Notary, L=London, C=GB"`

After the flow completes, find the game ID by using the following command:
`run vaultQuery contractStateType: com.poker.states.GameState`

That will come up with the output as the following:
     
    Fri Jun 21 15:15:17 BST 2019>>> run vaultQuery contractStateType: com.poker.states.GameState
    states:
    - state:
        data: !<com.poker.states.GameState>
          linearId:
            externalId: null
            id: "9d315cc4-88f5-48d1-8b72-c262bf785b12"
    
The `9d315cc4-88f5-48d1-8b72-c262bf785b12` is the game id in the above example. Note down the id you see in the terminal.

When you run multiple start games, you may see more than one game states. Note that you have to be careful to choose which of the games you wanted to play.

Now that you have successfully created a game. Lets add players to the game. Currently, only two nodes are present in the network. Lets add whoever is available.

On the Dealer Node, run the following two commands - one at a time.

`flow start com.poker.flows.AddPlayerFlow gameID: <GameID>, player: "O=PlayerA,L=London,C=GB"`

`flow start com.poker.flows.AddPlayerFlow gameID: <GameID>, player: "O=PlayerB,L=New York,C=US"`

The above commands would have added the two players into the game you wanted them to join.

If you would like to check, if you have added the players, you can run the vault query of Game State on Dealer or the players

You will see the following output:

    Fri Jun 21 15:15:17 BST 2019>>> run vaultQuery contractStateType: com.poker.states.GameState
    states:
    - state:
        data: !<com.poker.states.GameState>
          linearId:
            externalId: null
            id: "9d315cc4-88f5-48d1-8b72-c262bf785b12"
          dealer: "O=Dealer, L=New York, C=US"
          players:
            - "O=PlayerA, L=London, C=GB"
            - "O=PlayerB, L=New York, C=US"
          deckIdentifier:
            externalId: null
            id: "f0430858-6298-45e9-9d52-21cf49aead25"

You will notice that the Game State has the players. The game also has a unique Deck of cards.

Now lets start the betting

On each of the Player terminals, run the following command:
`flow start com.poker.flows.AddBettingAmountFlow gameID: <GameID>, amount: 100`

Feel free to increase the betting amount. Add betting all through the game if you are confident of the win.

Now, lets do the poker rounds.
On the Dealer terminal, run the following command.
`flow start com.poker.flows.PlayFLow gameID: <GameID>, round: Dealt`

The above command would issue two cards to the players. If the player would want to see the cards, go to their terminals and run the following command:
`run vaultQuery contractStateType: com.poker.states.PlayerState`

You will find the output as this:

    Fri Jun 21 15:14:47 BST 2019>>> run vaultQuery contractStateType: com.poker.states.PlayerState
    states:
    - state:
        data: !<com.poker.states.PlayerState>
          linearId:
            externalId: null
            id: "3c977ceb-c728-4197-97ec-cae5d3289430"
          party: "O=PlayerA, L=London, C=GB"
          dealer: "O=Dealer, L=New York, C=US"
          myCards:
          - suit: "SPADES"
            rank: "CARD_6"
          - suit: "HEARTS"
            rank: "CARD_5"
          rankingEnum: "HIGH_CARD"
          highCard:
            suit: "SPADES"
            rank: "CARD_10"
          highCardRankingList:
          - suit: "SPADES"
            rank: "CARD_10"
        contract: "com.poker.contracts.PokerContract"
        notary: "O=Notary, L=London, C=GB"
        encumbrance: null

The `myCards` is the card issued to the player. Run the same command in other Players terminal to find the cards.
The player can also compare the table cards and find if she has got a rank. That is available in `rankingEnum`. 
The ranking cards can be seen the highCardRankingList. Use this suggestion to increase the betting amount.

On the Dealer node, you can the following commands to play the subsequent rounds. After each round, find the player's state to see the state of their hand.

`flow start com.poker.flows.PlayFLow gameID: <GameID>, round: Flopped`

`flow start com.poker.flows.PlayFLow gameID: <GameID>, round: Rivered`

`flow start com.poker.flows.PlayFLow gameID: <GameID>, round: Turned`

Now, its the winner time. On the Dealer node, find the winner by running the following command:

`flow start com.poker.flows.PlayFLow gameID: <GameID>, round: Winner`
 
 The winner is displayed in the terminal
 
### Known issues:

The game is ready only for a demo stage. 

There is no error handling. 

The smart contracts are written only for start game.
 
 
### Useful commands in the shell
flow start com.poker.flows.StartGameFlow notary: "O=Notary, L=London, C=GB"

flow start com.poker.flows.AddPlayerFlow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, player: "O=PlayerA,L=London,C=GB"

flow start com.poker.flows.AddPlayerFlow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, player: "O=PlayerB,L=New York,C=US"

flow start com.poker.flows.PlayFLow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, round: Dealt

flow start com.poker.flows.PlayFLow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, round: Flopped

flow start com.poker.flows.PlayFLow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, round: Rivered

flow start com.poker.flows.PlayFLow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, round: Turned

flow start com.poker.flows.PlayFLow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, round: Winner

flow start com.poker.flows.AddBettingAmountFlow gameID: fad727a1-c1b7-41d3-8eb6-39863080606b, amount: 100

run vaultQuery contractStateType: com.poker.states.GameState

run vaultQuery contractStateType: com.poker.states.Deck

run vaultQuery contractStateType: com.poker.states.PlayerState

gameID can be found by running the vault query commands. The linerid is the game id

### Credits
https://github.com/phstc/javapokertexasholdem

Corda Code Club organizer : Martin Jee

Corda Code Club panel of mentors : Fuzz, Cais and Tudor 
