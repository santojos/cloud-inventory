package oci

import com.oracle.bmc.loadbalancer.LoadBalancerClient
import com.oracle.bmc.loadbalancer.model.LoadBalancer
import com.oracle.bmc.loadbalancer.requests.ListLoadBalancersRequest
import javax.inject.Inject
import mu.KotlinLogging
import oci.data.PublicIPDetails
import java.util.function.Consumer
import javax.inject.Named

private val logger = KotlinLogging.logger {}

/*
* <ul>
 * <li>A public IP address directly on the VNIC</li>
 * <li>Public IP addresses assigned to any secondary private IP on the VNIC</li>
 * </ul>
 */
class LoadBalancerPublicIP @Inject constructor(
    @Named("OciLoadBalancerClient") val loadBalancerClient: LoadBalancerClient
) : PublicIP {

    /*
    List instances in a compartment
    */
    private fun getLoadBalancer(compartmentId: String, region: String): List<LoadBalancer> {

        logger.info { "Getting loabalancers in compartment $compartmentId" }
        val loadBalancersList = ArrayList<LoadBalancer>()

        val listLoadBalancersRequest = ListLoadBalancersRequest.builder()
            .compartmentId(compartmentId)
            .lifecycleState(LoadBalancer.LifecycleState.Active)
            .limit(100L).build()

        loadBalancerClient.setRegion(region)
        loadBalancerClient.paginators.listLoadBalancersResponseIterator(listLoadBalancersRequest)
            .forEach { loadBalancersList.addAll(it.items) }
        logger.info { "Got ${loadBalancersList.size} loadbalancer in compartment $compartmentId" }
        return loadBalancersList
    }

    /**
     * Get Public Ips associated with Vnic attachments
     */
    private fun getPublicIps(loadbalancers: List<LoadBalancer>, region: String): Set<PublicIPDetails> {

        /*
    * Handles the scenario where public IP addresses are assigned to
    * secondary private IPs on the VNIC . First we find all private IPs
    * associated with the VNIC and for each of those try and find the
    * public IP (if any) which has been associated with the private IP
    */

        var publicIps = HashSet<PublicIPDetails>()

        loadbalancers.forEach {
            val loadBalancer = it
            logger.info { "Getting Public IP for loadbalancer with id ${it.id}" }
             loadBalancer.ipAddresses.forEach(Consumer { ipAddress ->
                 if(ipAddress.isPublic){

                     val publicIPDetails = PublicIPDetails(it.compartmentId,
                         loadBalancer.id,
                         "NA",
                         "NA",
                         ipAddress.ipAddress,
                         region)

                     publicIps.add(publicIPDetails)
                 }
             })
        }

        return publicIps
    }

    /**
     * prints list of public Ips with tenancy
     */
    override fun fetchPublicIP(compartmentId: String, regions: List<String>): Set<PublicIPDetails> {
        var publicIps = HashSet<PublicIPDetails>()

        regions.forEach {
            val publicIpSet = getPublicIps(getLoadBalancer(compartmentId, it), it)
            publicIps.addAll(publicIpSet)
        }
        return publicIps
    }
}

