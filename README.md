<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# CorDapp Poker - Texas-Hold-Em

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

### Client

`clients/src/main/kotlin/com/poker/Client.kt` defines a simple command-line client that connects to a node via RPC 
and prints a list of the other nodes on the network.

#### Running the client

##### Via the command line

Run the `runPokerClient` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`.

##### Via IntelliJ

Run the `Run Poker Client` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`.

### Webserver

`clients/src/main/kotlin/com/poker/webserver/` defines a simple Spring webserver that connects to a node via RPC and 
allows you to interact with the node over HTTP.

The API endpoints are defined here:

     clients/src/main/kotlin/com/poker/webserver/Controller.kt

And a static webpage is defined here:

     clients/src/main/resources/static/

#### Running the webserver

##### Via the command line

Run the `runPokerServer` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

##### Via IntelliJ

Run the `Run Poker Server` run configuration. By default, it connects to the node with RPC address `localhost:10006` 
with the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

#### Interacting with the webserver

The static webpage is served on:

    http://localhost:10050

While the sole poker endpoint is served on:

    http://localhost:10050/pokerendpoint

For a guided example of how to extend this poker, see the Hello, World! tutorial 
[here](https://docs.corda.net/hello-world-introduction.html).

### Useful commands in the shell
flow start com.poker.flows.StartGameFlow notary: "O=Notary, L=London, C=GB"

flow start com.poker.flows.AddPlayerFlow gameID: 211462d1-55b2-4316-aa0b-317c892d0c60, player: "O=PlayerA,L=London,C=GB"

flow start com.poker.flows.AddPlayerFlow gameID: 211462d1-55b2-4316-aa0b-317c892d0c60, player: "O=PlayerB,L=New York,C=US"

run vaultQuery contractStateType: com.poker.states.GameState

run vaultQuery contractStateType: com.poker.states.Deck

run vaultQuery contractStateType: com.poker.states.Player

gameID can be found by running the vault query commands. The linerid is the game id