package auth.oci

import com.oracle.bmc.ConfigFileReader
import com.oracle.bmc.auth.AuthenticationDetailsProvider
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class OCIAuthProvider {

    private var configFilePath = "~/.oci/config"
    private var profile = "DEFAULT"

    /**
     * Using Config file Provider at present
     */
    fun authProvider(): AuthenticationDetailsProvider {

        logger.info { " Configuring auth provider for OCI " }

        val configFile = ConfigFileReader.parse(configFilePath, profile)
        return ConfigFileAuthenticationDetailsProvider(configFile)
    }
}