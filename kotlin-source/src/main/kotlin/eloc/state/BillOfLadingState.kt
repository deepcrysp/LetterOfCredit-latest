package eloc.state

import eloc.LetterOfCreditDataStructures.Company
import eloc.LetterOfCreditDataStructures.Good
import eloc.LetterOfCreditDataStructures.Location
import eloc.LetterOfCreditDataStructures.Person
import eloc.LetterOfCreditDataStructures.Port
import eloc.LetterOfCreditDataStructures.Weight
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.io.InputStream
import java.time.Instant
import java.time.LocalDate

data class BillOfLadingState(
        val owner: Party,
        val seller: Party,
        val buyer: Party,
        val advisory: Party,
        val issuer: Party,
        val timestamp: Instant,
        val props: BillOfLadingProperties) : LinearState {
    override val linearId = UniqueIdentifier(props.billOfLadingID)
    override val participants = listOf(owner, buyer, advisory, issuer)
}

@CordaSerializable
data class BillOfLadingProperties(
        val billOfLadingID: String,
        val issueDate: LocalDate,
        val carrierOwner: String,
        val nameOfVessel: String,
        val descriptionOfGoods: List<Good>,
        val portOfLoading: Port,
        val portOfDischarge: Port,
        val grossWeight: Weight,
        val dateOfShipment: LocalDate?,
        val shipper: Company?,
        val notify: Person?,
        val consignee: Company?,
        val placeOfReceipt: Location?)