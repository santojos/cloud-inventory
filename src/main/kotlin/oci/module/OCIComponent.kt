package oci.module


import dagger.Component
import oci.InstancePublicIP
import oci.LoadBalancerPublicIP
import javax.inject.Singleton

@Singleton
@Component(modules = [(OCIModule::class)])
interface OCIComponent {

    fun instancePublicIP(): InstancePublicIP
    fun loadbalancerPublicIP(): LoadBalancerPublicIP
}