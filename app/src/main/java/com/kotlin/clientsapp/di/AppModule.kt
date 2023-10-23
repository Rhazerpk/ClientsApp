package com.kotlin.clientsapp.di
import com.kotlin.clientsapp.data.remote.dto.ClientApi
import com.kotlin.clientsapp.data.repository.ClientRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideClientApi(moshi: Moshi): ClientApi {
        return Retrofit.Builder()
            .baseUrl("http://ClientCareAPI.somee.com")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ClientApi::class.java)
    }

    @Provides
    @Singleton
    fun provideClientRepository(clientApi: ClientApi): ClientRepository {
        return ClientRepository(clientApi)
    }


}