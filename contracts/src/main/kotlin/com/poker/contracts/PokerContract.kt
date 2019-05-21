package com.poker.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************
class PokerContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        @JvmStatic
        val ID = PokerContract::class.qualifiedName!!
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Start_GAME : Commands

        class PlaceSmallBlindBet : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "On place small bet, no input states must be consumed."
            }
        }

        class PlaceBigBet : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class TwoFaceDownToPlayer : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class DecideBet : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class ThreeCards : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class OneCard : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class OnCall : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class OnRaise : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
        class OnFold : Commands {
            companion object {
                const val CONTRACT_RULE_INPUTS =
                        "TODO"
            }
        }
    }
}