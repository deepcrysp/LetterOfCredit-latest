package eloc.state

import eloc.LetterOfCreditDataStructures.CreditType
import eloc.LetterOfCreditDataStructures.Location
import eloc.LetterOfCreditDataStructures.Port
import eloc.LetterOfCreditDataStructures.PricedGood
import net.corda.core.contracts.*
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.LocalDate
import java.time.Period
import java.util.*

data class LetterOfCreditApplicationState(
        val applicant: Party,
        val issuer: Party,
        val beneficiary: Party,
        val advisingBank: Party,
        val props: LetterOfCreditApplicationProperties) : LinearState {
    override val participants = listOf(applicant, issuer)
    override val linearId = UniqueIdentifier(props.letterOfCreditApplicationID)
}

@CordaSerializable
data class LetterOfCreditApplicationProperties(
        val letterOfCreditApplicationID: String,
        val applicationDate: LocalDate,
        val typeCredit: CreditType,
        val expiryDate: LocalDate,
        val portLoading: Port,
        val portDischarge: Port,
        val placePresentation: Location,
        val lastShipmentDate: LocalDate,
        val periodPresentation: Period,
        val descriptionGoods: List<PricedGood> = ArrayList(),
        val documentsRequired: List<String> = ArrayList(),
        val amount: Amount<Currency>)