### Aim

To fetch all the public ips associated with a tenancy

### How it works 
* Read a list of json file with tenant Ids
* Fetches Public IP Documents associated with the tenants
* List Public IPs for a tenant

### Current support

### OCI
* Public IP attached to instances
* Public IP on a Load balancer

### How to run

Update tenant.json with compartment and tenant info 

```
gradle clean build
gradle run --args  ~/cloud-inventory/src/main/resources/tenant.json
```

### Configuration
* ~/.oci/config with EXPLORE profile, to change configuration update `auth.oci.OCIAuthProvider` file

```
[EXPLORE]
tenancy=ocid1.tenancy.oc1..bogus.tenency.ocid
user=ocid1.user.bogus.user.id
key_file=~/.oci/oci_api_key.pem
fingerprint=3b:79:93:90:ba:f2:68:any:finger:print
```
