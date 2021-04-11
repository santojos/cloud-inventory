package oci.module


import dagger.Component
import oci.InstancePublicIp
import javax.inject.Singleton

@Singleton
@Component(modules = [(OCIModule::class)])
interface OCIComponent {

    fun instancePublicIP(): InstancePublicIp
}