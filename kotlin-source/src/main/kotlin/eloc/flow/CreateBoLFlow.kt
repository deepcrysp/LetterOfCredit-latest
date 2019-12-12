package eloc.flow

import co.paralleluniverse.fibers.Suspendable
import eloc.contract.BillOfLadingContract
import eloc.contract.LetterOfCreditContract
import eloc.state.BillOfLadingProperties
import eloc.state.BillOfLadingState
import eloc.state.LetterOfCreditState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.time.Duration
import java.time.Instant

@InitiatingFlow
@StartableByRPC
class CreateBoLFlow(val buyerName: String, val advisingBankName: String, val issuingBankName: String,
                    val billOfLadingProperties: BillOfLadingProperties) : FlowLogic<SignedTransaction>() {

    override val progressTracker = ProgressTracker(GETTING_NOTARY, GETTING_COUNTERPARTIES, GENERATING_TRANSACTION,
            VERIFYING_TRANSACTION, SIGNING_TRANSACTION, FINALISING_TRANSACTION)

    @Suspendable
    override fun call(): SignedTransaction {
        progressTracker.currentStep = GETTING_NOTARY
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        progressTracker.currentStep = GETTING_COUNTERPARTIES
        val buyer = serviceHub.identityService.partiesFromName(buyerName, false).singleOrNull()
                ?: throw IllegalArgumentException("No exact match found for seller name $buyerName.")
        val advisingBank = serviceHub.identityService.partiesFromName(advisingBankName, false).singleOrNull()
                ?: throw IllegalArgumentException("No exact match found for advising bank name $advisingBankName.")
        val issuingBank = serviceHub.identityService.partiesFromName(issuingBankName, false).singleOrNull()
                ?: throw IllegalArgumentException("No exact match found for issuing bank name $issuingBankName.")

        progressTracker.currentStep = GENERATING_TRANSACTION
        val billOfLading = BillOfLadingState(ourIdentity, ourIdentity, buyer, advisingBank, issuingBank, Instant.now(), billOfLadingProperties)

        // TODO: Can change this to querying using a schema.
        val id = billOfLadingProperties.billOfLadingID
        val lettersOfCredit = serviceHub.vaultService.queryBy<LetterOfCreditState>().states
        val letterOfCreditStateAndRef = lettersOfCredit.find { stateAndRef -> stateAndRef.state.data.props.letterOfCreditID == id }
                ?: throw IllegalArgumentException("No letter of credit with ID $id found.")

        val outputLetterOfCredit = letterOfCreditStateAndRef.state.data.laded()

        val builder = TransactionBuilder(notary)
        builder.setTimeWindow(Instant.now(), Duration.ofSeconds(60))

        val issueCommand = Command(BillOfLadingContract.Commands.Issue(), ourIdentity.owningKey)
        val addBillOfLadingCommand = Command(LetterOfCreditContract.Commands.AddBillOfLading(), ourIdentity.owningKey)

        builder.addInputState(letterOfCreditStateAndRef)
        builder.addOutputState(billOfLading, BillOfLadingContract.CONTRACT_ID)
        builder.addOutputState(outputLetterOfCredit, LetterOfCreditContract.CONTRACT_ID)
        builder.addCommand(issueCommand)
        builder.addCommand(addBillOfLadingCommand)

        progressTracker.currentStep = VERIFYING_TRANSACTION
        builder.verify(serviceHub)

        progressTracker.currentStep = SIGNING_TRANSACTION
        val stx = serviceHub.signInitialTransaction(builder)

        progressTracker.currentStep = FINALISING_TRANSACTION
        return subFlow(FinalityFlow(stx))
    }
}