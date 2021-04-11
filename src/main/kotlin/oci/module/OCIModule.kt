package oci.module;

import auth.oci.OCIAuthProvider
import com.oracle.bmc.auth.AuthenticationDetailsProvider
import com.oracle.bmc.core.ComputeClient
import com.oracle.bmc.core.VirtualNetworkClient
import com.oracle.bmc.loadbalancer.LoadBalancer
import com.oracle.bmc.loadbalancer.LoadBalancerClient
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module
class OCIModule {

    @Provides
    @Singleton
    @Named("OciComputeClient")
    fun providesComputeClient(provider: AuthenticationDetailsProvider): ComputeClient = ComputeClient(provider)

    @Provides
    @Singleton
    @Named("OciVirtualNetworkClient")
    fun providesVirtualNetworkClient(provider: AuthenticationDetailsProvider): VirtualNetworkClient = VirtualNetworkClient(provider)

    @Provides
    @Singleton
    @Named("OciLoadBalancerClient")
    fun providesLoadBalancerClient(provider: AuthenticationDetailsProvider): LoadBalancerClient = LoadBalancerClient(provider)

    @Provides
    @Singleton
    fun providesAuthProviderOCI(): AuthenticationDetailsProvider = OCIAuthProvider().authProvider()
}