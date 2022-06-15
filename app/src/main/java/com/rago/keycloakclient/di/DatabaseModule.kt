package com.rago.keycloakclient.di

import android.content.Context
import androidx.room.Room
import com.rago.keycloakclient.db.MyDatabase
import com.rago.keycloakclient.db.dao.ShipperDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyDatabase =
        Room.databaseBuilder(context, MyDatabase::class.java, "MY_DATABASE").build()

    @Provides
    @Singleton
    fun provideShipperDao(myDatabase: MyDatabase): ShipperDao = myDatabase.shipperDao()
}