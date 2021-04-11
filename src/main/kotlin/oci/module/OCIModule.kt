package oci.module;

import auth.oci.OCIAuthProvider
import com.oracle.bmc.auth.AuthenticationDetailsProvider
import com.oracle.bmc.core.ComputeClient
import com.oracle.bmc.core.VirtualNetworkClient
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module
class OCIModule {

    @Provides
    @Singleton
    @Named("OciComputeClient")
    fun providesComputeClient(provider: AuthenticationDetailsProvider): ComputeClient {
        val computeClient = ComputeClient(provider)
        computeClient.setRegion("us-ashburn-1")
        return computeClient
    }

    @Provides
    @Singleton
    @Named("OciVirtualNetworkClient")
    fun providesVirtualNetworkClient(provider: AuthenticationDetailsProvider): VirtualNetworkClient {
        val virtualNetworkClient = VirtualNetworkClient(provider)
        virtualNetworkClient.setRegion("us-ashburn-1")
        return virtualNetworkClient;
    }

    @Provides
    @Singleton
    fun providesAuthProviderOCI(): AuthenticationDetailsProvider = OCIAuthProvider().authProvider()
}