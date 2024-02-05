package com.virser.testapp.di

import com.virser.testapp.data.api.ApiCallHandler
import com.virser.testapp.data.api.CommonApiCallHandler
import com.virser.testapp.data.api.ZippoAppsApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideZippoAppsApiClient(retrofit: Retrofit): ZippoAppsApi =
        retrofit.create(ZippoAppsApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        moshi: Moshi,
    ): Retrofit = getRetrofit(client, moshi)

    @Singleton
    @Provides
    fun provideHttpClient(
    ): OkHttpClient = OkHttpClient.Builder().readTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS)
        .callTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS).build()

    private fun getRetrofit(
        client: OkHttpClient,
        moshi: Moshi,
    ): Retrofit = Retrofit.Builder().client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi).withNullSerialization())
        .baseUrl(HOST).build()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun provideApiCallHandler(): ApiCallHandler = CommonApiCallHandler()

    companion object {
        const val HOST = "https://zipoapps-storage-test.nyc3.digitaloceanspaces.com/"
        const val TIMEOUT_DEFAULT = 30L
    }
}
