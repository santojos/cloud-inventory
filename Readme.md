### Experiments with Kotlin

* Read a list of json file with tenant Ids
* Fetches Public IP Documents associated with the tenants
* List Public IPs for a tenant


### How to run

Update tenant.json with compartment and tenant info 

gradle build

gradle run --args  ~/cloud-inventory/src/main/resources/tenant.json
