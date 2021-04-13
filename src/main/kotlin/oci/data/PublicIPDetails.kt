package oci.data

data class PublicIPDetails(
    val compartmentId: String,
    val displayname: String,
    val instanceId: String,
    val vnicId: String,
    val privateIP: String,
    val publicIP: String,
    val region: String,
    val type : String
)