@file:JvmName("InventoryCrawler")

package com.example

import java.nio.file.Paths
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader



/**
 * 1. Read the file with tenant configuration
 * 2. Fetch
 */
fun main(args: Array<String>  ) {

    val path = Paths.get(args.get(0))
    println("file is ${path.toFile().name}")
    val bufferedReader: BufferedReader = path.toFile().bufferedReader()
    val tenantConfigString = bufferedReader.use { it.readText() }

    var gson = Gson()
    val listType = object : TypeToken<List<TenantConfig>>() { }.type
    val tenantConfigList = gson.fromJson<List<TenantConfig>>(tenantConfigString, listType)
    tenantConfigList.forEach {
        println("Tenant config is ${it.tenantId} : ${it.clouds}")
        fetchPublicIP(it.tenantId)


    }

}

fun fetchPublicIP(tenantId: String){
    println("Fetching Public IP for tenant ${tenantId}")
}
/**
 * Data Classes
 */
data class TenantConfig(val tenantId: String, val clouds: List<String>)
