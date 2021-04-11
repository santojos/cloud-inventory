import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.internal.DaggerCollections
import mu.KotlinLogging
import oci.data.PublicIPDetails
import oci.data.TenantConfig
import oci.module.DaggerOCIComponent
import oci.module.OCIComponent
import oci.module.OCIModule
import java.io.BufferedReader
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

class KotlinApplication {

    companion object {
        /**
         * 1. Read the file with tenant configuration
         * 2. Fetch
         */
        @JvmStatic
        fun main(args: Array<String>) {

            val main = KotlinApplication()
            val ociComponent = main.getOciComponent()

            val path = Paths.get(args.get(0))
            logger.info { "file is ${path.toFile().name}" }
            val bufferedReader: BufferedReader = path.toFile().bufferedReader()
            val tenantConfigString = bufferedReader.readText()

            var gson = Gson()
            val listType = object : TypeToken<List<TenantConfig>>() {}.type
            val tenantConfigList = gson.fromJson<List<TenantConfig>>(tenantConfigString, listType)

            tenantConfigList.forEach {

                val publicIPSet = HashSet<PublicIPDetails>()
                logger.info("Tenant config is ${it.tenantId} : ${it.compartmentId} :  ${it.provider}")
                publicIPSet.addAll(ociComponent.instancePublicIP().fetchPublicIP(it.compartmentId, it.regions))
                publicIPSet.addAll(ociComponent.loadbalancerPublicIP().fetchPublicIP(it.compartmentId, it.regions))


                logger.info { "--------------------------------------------------------------------------------" }
                if (publicIPSet.isNotEmpty()) {
                    logger.info("Public IP Details for compartment ${it.compartmentId} are :: ")
                    publicIPSet.forEach { logger.info { it } }
                } else {
                    logger.info { "No Public Ip in tenency ${it.tenantId}" }
                }
                logger.info { "--------------------------------------------------------------------------------" }
            }
        }
    }

    fun getOciComponent(): OCIComponent {
        return DaggerOCIComponent.builder().oCIModule(OCIModule()).build()
    }

}



