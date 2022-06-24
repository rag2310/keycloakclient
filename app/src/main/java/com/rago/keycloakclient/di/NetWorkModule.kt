package com.rago.keycloakclient.di

import com.rago.keycloakclient.network.KeycloackApiService
import com.rago.keycloakclient.utils.AuthStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetWorkModule {

    @Singleton
    @Provides
    fun provideOkhttpClient(authStateManager: AuthStateManager): OkHttpClient {
        val authorizationInterceptor = Interceptor { chain ->
            var request: Request = chain.request()
            val token = authStateManager.getInstance().accessToken
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token").build()
            return@Interceptor chain.proceed(request)
        }

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().addInterceptor(logInterceptor)
            .addInterceptor(authorizationInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://sso-dev.trackchain.io/auth/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }


    @Singleton
    @Provides
    fun provideKeycloackApiService(retrofit: Retrofit): KeycloackApiService =
        retrofit.create(KeycloackApiService::class.java)
}