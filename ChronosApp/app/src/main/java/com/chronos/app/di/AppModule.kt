package com.chronos.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    // Add Room DB, Retrofit, Repository providers here as the app grows:
    //
    // @Provides @Singleton
    // fun provideDatabase(@ApplicationContext ctx: Context): ChronosDatabase =
    //     Room.databaseBuilder(ctx, ChronosDatabase::class.java, "chronos_db").build()
    //
    // @Provides @Singleton
    // fun provideAlarmDao(db: ChronosDatabase): AlarmDao = db.alarmDao()
    //
    // @Provides @Singleton
    // fun provideWeatherApi(): WeatherApi = Retrofit.Builder()
    //     .baseUrl("https://api.openweathermap.org/data/2.5/")
    //     .addConverterFactory(GsonConverterFactory.create())
    //     .build()
    //     .create(WeatherApi::class.java)
}
