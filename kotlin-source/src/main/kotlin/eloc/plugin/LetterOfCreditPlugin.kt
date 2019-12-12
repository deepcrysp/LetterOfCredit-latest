package eloc.plugin

import eloc.api.LetterOfCreditApi
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function

/**
 * This class defines the plugin for the ELOC application.
 */
class LetterOfCreditPlugin : WebServerPluginRegistry {
    override val webApis = listOf(Function(::LetterOfCreditApi))
    override val staticServeDirs = mapOf(
            "loc" to javaClass.classLoader.getResource("loc").toExternalForm(),
            "loc/seller" to javaClass.classLoader.getResource("loc").toExternalForm(),
            "loc/seller" to javaClass.classLoader.getResource("loc").toExternalForm(),
            "loc/advising" to javaClass.classLoader.getResource("loc").toExternalForm(),
            "loc/issuing" to javaClass.classLoader.getResource("loc").toExternalForm()
    )
}