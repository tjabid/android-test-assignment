package com.example.shacklehotelbuddy.di

import com.example.shacklehotelbuddy.BuildConfig
import com.example.shacklehotelbuddy.data.remote.interceptor.CurlLoggingInterceptor
import com.example.shacklehotelbuddy.data.remote.service.HotelSearchService
import com.example.shacklehotelbuddy.domain.core.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @Named(AppConstants.OKHTTP_REQUEST_HEADER_INTERCEPTOR)
    fun provideRequestInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .header(AppConstants.HEADER_RAPID_API_KEY, BuildConfig.RAPID_HEADER_API_KEY)
            .header(AppConstants.HEADER_RAPID_API_HOST, BuildConfig.RAPID_HEADER_HOST)
            .header(AppConstants.HEADER_CONTENT_TYPE, AppConstants.HEADER_CONTENT_TYPE_VALUE)
            .build()
        chain.proceed(request)
    }

    @Singleton
    @Provides
    @Named(AppConstants.OKHTTP_LOGGING_INTERCEPTOR)
    fun provideHttpLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) Level.BODY else Level.NONE)

    @Singleton
    @Provides
    @Named(AppConstants.OKHTTP_CURL_INTERCEPTOR)
    fun provideCurlInterceptor(): Interceptor = CurlLoggingInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @Named(AppConstants.OKHTTP_REQUEST_HEADER_INTERCEPTOR) requestInterceptor: Interceptor,
        @Named(AppConstants.OKHTTP_LOGGING_INTERCEPTOR) loggingInterceptor: Interceptor,
        @Named(AppConstants.OKHTTP_CURL_INTERCEPTOR) curlInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addInterceptor(curlInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideHotelSearchService(retrofit: Retrofit): HotelSearchService = retrofit.create(
        HotelSearchService::class.java
    )
}