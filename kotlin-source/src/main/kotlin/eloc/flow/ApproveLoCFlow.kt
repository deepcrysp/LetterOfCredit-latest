package eloc.flow

import co.paralleluniverse.fibers.Suspendable
import eloc.contract.LetterOfCreditApplicationContract
import eloc.contract.LetterOfCreditContract
import eloc.contract.PurchaseOrderContract
import eloc.state.*
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.seconds
import java.time.LocalDate

@InitiatingFlow
@StartableByRPC
class ApproveLoCFlow(val reference: String) : FlowLogic<SignedTransaction>() {

    override val progressTracker = ProgressTracker(GETTING_NOTARY, GENERATING_TRANSACTION, VERIFYING_TRANSACTION,
            SIGNING_TRANSACTION, FINALISING_TRANSACTION)

    @Suspendable
    override fun call(): SignedTransaction {
        progressTracker.currentStep = GETTING_NOTARY
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        progressTracker.currentStep = GENERATING_TRANSACTION
        val applicationStateAndRef = serviceHub.vaultService.queryBy<LetterOfCreditApplicationState>().states.find {
            it.state.data.props.letterOfCreditApplicationID == reference
        } ?: throw IllegalArgumentException("No letter-of-credit application with ID $reference found.")

        val purchaseOrderStateAndRef = serviceHub.vaultService.queryBy<PurchaseOrderState>().states.find {
            it.state.data.props.purchaseOrderID == reference
        } ?: throw IllegalArgumentException("No purchase order with ID $reference found.")

        val application = applicationStateAndRef.state.data

        val locProps = LetterOfCreditProperties(application.props, LocalDate.now())

        val loc = LetterOfCreditState(
                application.beneficiary,
                application.advisingBank,
                application.issuer,
                application.applicant,
                status = LetterOfCreditStatus.ISSUED,
                props = locProps)

        val builder = TransactionBuilder(notary = notary)
                .addInputState(applicationStateAndRef)
                .addInputState(purchaseOrderStateAndRef)
                .addOutputState(loc, LetterOfCreditContract.CONTRACT_ID)
                .addCommand(LetterOfCreditApplicationContract.Commands.Approve(), ourIdentity.owningKey)
                .addCommand(LetterOfCreditContract.Commands.Issue(), ourIdentity.owningKey)
                .addCommand(PurchaseOrderContract.Commands.Extinguish(), ourIdentity.owningKey)

        progressTracker.currentStep = SIGNING_TRANSACTION
        val currentTime = serviceHub.clock.instant()
        builder.setTimeWindow(currentTime, 30.seconds)

        progressTracker.currentStep = VERIFYING_TRANSACTION
        builder.verify(serviceHub)

        progressTracker.currentStep = SIGNING_TRANSACTION
        val stx = serviceHub.signInitialTransaction(builder)

        progressTracker.currentStep = FINALISING_TRANSACTION
        return subFlow(FinalityFlow(stx))
    }
}