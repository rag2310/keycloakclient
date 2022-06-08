package com.rago.keycloakclient.di

import android.content.Context
import com.rago.keycloakclient.utils.AuthStateManager
import com.rago.keycloakclient.utils.AuthorizationServiceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideAuthStateManager(@ApplicationContext context: Context) = AuthStateManager(context)

    @Singleton
    @Provides
    fun provideAuthorizationService(@ApplicationContext context: Context) = AuthorizationService(context)
}