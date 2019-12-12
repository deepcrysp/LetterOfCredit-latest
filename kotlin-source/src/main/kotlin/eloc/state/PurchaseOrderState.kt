package eloc.state

import eloc.LetterOfCreditDataStructures.Company
import eloc.LetterOfCreditDataStructures.PricedGood
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.LocalDate

data class PurchaseOrderState(
        val owner: Party,
        val seller: Party,
        val consumable: Boolean,
        val props: PurchaseOrderProperties,
        override val participants: List<AbstractParty> = listOf(owner, seller)) : LinearState {
    override val linearId = UniqueIdentifier()
}

@CordaSerializable
data class PurchaseOrderProperties(
        val purchaseOrderID: String,
        val seller: Company,
        val buyer: Company,
        val purchaseOrderDate: LocalDate,
        val term: Long,
        val goods: List<PricedGood>) {
    val payDate = purchaseOrderDate.plusDays(term)
}