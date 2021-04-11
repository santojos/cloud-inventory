package oci

import com.oracle.bmc.core.ComputeClient
import com.oracle.bmc.core.VirtualNetworkClient
import com.oracle.bmc.core.model.GetPublicIpByPrivateIpIdDetails
import com.oracle.bmc.core.model.Instance
import com.oracle.bmc.core.model.VnicAttachment
import com.oracle.bmc.core.requests.*
import com.oracle.bmc.model.BmcException
import javax.inject.Inject
import mu.KotlinLogging
import oci.data.PublicIPDetails
import javax.inject.Named

private val logger = KotlinLogging.logger {}

class InstancePublicIp @Inject constructor(
    @Named("OciComputeClient") val computeClient: ComputeClient,
    @Named("OciVirtualNetworkClient") val vcnClient: VirtualNetworkClient
) {

    /*
    List instances in a compartment
    */
    private fun getInstances(compartmentId: String): List<Instance> {

        logger.info { "Getting instances for compartment with id $compartmentId" }
        val instances = ArrayList<Instance>()
        val listInstancesRequest = ListInstancesRequest.builder().compartmentId(compartmentId)
            .lifecycleState(Instance.LifecycleState.Running)
            .limit(100)
            .build()

        computeClient.paginators.listInstancesResponseIterator(listInstancesRequest)
            .forEach { instances.addAll(it.items) }
        logger.info { "Got ${instances.size} instance in compartment $compartmentId" }
        return instances
    }

    /**
     * List all Vnic Attachments for all instances in a compartment
     */
    private fun getVnicAttachment(compartmentId: String, instances: List<Instance>): List<VnicAttachment> {

        logger.info { "Getting vnic for ${instances.size} instances in compartment : $compartmentId" }

        val vnicAttachments = ArrayList<VnicAttachment>()

        instances.forEach { instance ->
            val listVnicAttachmentsRequest = ListVnicAttachmentsRequest.builder()
                .compartmentId(compartmentId)
                .instanceId(instance.id)
                .limit(100)
                .build()

            computeClient.paginators.listVnicAttachmentsResponseIterator(listVnicAttachmentsRequest)
                .forEach { vnicAttachments.addAll(it.items) }
        }

        logger.info { "Got ${vnicAttachments.size} vnicAttachments in compartment $compartmentId" }

        return vnicAttachments
    }

    /**
     * Get Public Ips associated with Vnic attachments
     */
    private fun getPublicIps(vnicAttachments: List<VnicAttachment>): Set<PublicIPDetails> {

        /*
    * Handles the scenario where public IP addresses are assigned to
    * secondary private IPs on the VNIC . First we find all private IPs
    * associated with the VNIC and for each of those try and find the
    * public IP (if any) which has been associated with the private IP
    */

        var publicIps = HashSet<PublicIPDetails>()

        vnicAttachments.forEach {
            val vnicAttachment = it
            logger.info { "Getting Public IP for vnic with id ${it.id}" }
            val getVnicResponsea = vcnClient.getVnic(GetVnicRequest.builder().vnicId(it.vnicId).build())

            if (getVnicResponsea.vnic.publicIp != null) {

                val publicIPDetails = PublicIPDetails(it.compartmentId,
                    vnicAttachment.instanceId,
                    it.vnicId,
                    "NA",
                    getVnicResponsea.vnic.publicIp)

                publicIps.add(publicIPDetails)

            }

            //get public ip from private ip
            val privateIPsList =
                vcnClient.paginators.listPrivateIpsRecordIterator(ListPrivateIpsRequest.builder().vnicId(it.vnicId)
                    .build())

            privateIPsList.forEach {
                try {
                    logger.info { "Getting Public IP for privateIP ${it.id} for vnic ${it.vnicId}" }
                    var getPublicIpResponse =
                        vcnClient.getPublicIpByPrivateIpId(
                            GetPublicIpByPrivateIpIdRequest.builder().getPublicIpByPrivateIpIdDetails(
                                GetPublicIpByPrivateIpIdDetails.builder().privateIpId(it.id).build()).build())


                    val publicIPDetails = PublicIPDetails(it.compartmentId,
                        vnicAttachment.instanceId,
                        it.vnicId,
                        it.ipAddress,
                        getPublicIpResponse.publicIp.ipAddress)

                    publicIps.add(publicIPDetails)
                } catch (ex: BmcException) {
                    if (ex.getStatusCode() == 404) {
                        //ignoring it as private IP address does not have a public IP
                        logger.info("No public IP for private IP ${it.ipAddress}")
                    } else {
                        logger.error { ex }
                    }
                }
            }
        }
        return publicIps
    }

    /**
     * prints list of public Ips with tenancy
     */
    fun getPublicIP(compartmentId: String) =
        getPublicIps(getVnicAttachment(compartmentId, getInstances(compartmentId)))

}

