package eloc

import com.wildfire.flow.CashFlow.Issue
import com.wildfire.flow.PledgeFlow.InitiatePledge
import com.wildfire.state.PledgeState
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startFlow
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import net.corda.finance.DOLLARS
import net.corda.finance.issuedBy
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.NodeHandle
import net.corda.testing.driver.PortAllocation
import net.corda.testing.driver.driver
import net.corda.testing.node.NotarySpec
import net.corda.testing.node.User
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("eloc.DriverKt")

    driver(DriverParameters(
            startNodesInProcess = true,
            waitForAllNodesToFinish = true,
            extraCordappPackagesToScan = listOf("net.corda.finance.contracts.asset", "net.corda.finance.schemas", "com.wildfire.contract"),
            notarySpecs = listOf(NotarySpec(CordaX500Name.parse("O=Notary Pool,L=Sao Paolo,C=BR"), validating = false)),
            networkParameters = testNetworkParameters(maxTransactionSize = Int.MAX_VALUE)),
            dsl = {
                val rpcUserList = listOf(User("user1", "test", permissions = setOf("ALL")))

                val names =
                        listOf("O=First Bank of London,L=London,C=GB", "O=Shenzhen State Bank,L=Shenzhen,C=CN", "O=Analog Importers,L=Liverpool,C=GB", "O=Lok Ma Exporters,L=Shenzhen,C=CN", "O=Central Bank,L=New York,C=US")

                val (bankOneFuture, bankTwoFuture, buyerFuture, sellerFuture, centralBankFuture) =
                        names.map { name -> startNode(providedName = CordaX500Name.parse(name), rpcUsers = rpcUserList) }

                val (bankOne, bankTwo, buyer, seller, centralBank) =
                        listOf(bankOneFuture, bankTwoFuture, buyerFuture, sellerFuture, centralBankFuture).map { future -> future.get() }

                val pledgeNonceOne = pledgeTenMillion(bankOne, centralBank)
                val pledgeNonceTwo = pledgeTenMillion(bankTwo, centralBank)
                confirmPledge(centralBank, pledgeNonceOne)
                confirmPledge(centralBank, pledgeNonceTwo)

                listOf(bankOne, bankTwo, buyer, seller, centralBank).map { node -> startWebserver(node) }.map { future -> future.get() }

                logger.info("Nodes started. Start the demo by going to http://localhost:10014/web/loc/.")
            })
}

private fun pledgeTenMillion(nodeHandle: NodeHandle, centralBankHandle: NodeHandle): Long {
    val me = nodeHandle.nodeInfo.legalIdentities.first()
    val centralBank = centralBankHandle.nodeInfo.legalIdentities.first()

    val pledgeState = PledgeState(
            10000000.DOLLARS.issuedBy(me.ref(0.toByte())),
            listOf(centralBank, me),
            me)

    val transaction = nodeHandle.rpc
            .startFlow(::InitiatePledge, pledgeState)
            .returnValue
            .getOrThrow()

    return transaction.tx.outputsOfType<PledgeState>().single().nonce
}

private fun confirmPledge(centralBankHandle: NodeHandle, nonce: Long) {
    centralBankHandle.rpc
            .startFlow(::Issue, nonce.toString())
            .returnValue
            .getOrThrow()
}
