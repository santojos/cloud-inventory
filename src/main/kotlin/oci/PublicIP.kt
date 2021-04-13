package oci

import oci.data.PublicIPDetails

interface PublicIP {

    /**
     * method to fetch public ip associated with cloud resource in a given compartment and regions
     */
    fun fetchPublicIP(compartment: String, regions: List<String> ): Map<String, PublicIPDetails>
}