package com.shivam.downn.di

import com.shivam.downn.data.api.SocialApi
import com.shivam.downn.data.api.AuthApi
import com.shivam.downn.data.api.AppSettingsApi
import com.shivam.downn.data.api.AuthInterceptor
import com.shivam.downn.data.api.ConnectivityInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

import com.shivam.downn.data.api.ProfileApi
import com.shivam.downn.data.api.NotificationApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            })
            .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            })
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        connectivityInterceptor: ConnectivityInterceptor,
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): OkHttpClient {
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        val cache = okhttp3.Cache(java.io.File(context.cacheDir, "http_cache"), cacheSize)

        val cacheInterceptor = okhttp3.Interceptor { chain ->
            var request = chain.request()
            // For GET requests, add cache headers
            request = if (com.shivam.downn.utils.NetworkUtils.isNetworkAvailable(context)) {
                request.newBuilder()
                    .header("Cache-Control", "public, max-age=${5 * 60}") // 5 min fresh
                    .build()
            } else {
                request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=${7 * 24 * 60 * 60}") // 7 days stale
                    .build()
            }
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(authInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.2:8081") // Use 10.0.2.2 for localhost from Android Emulator
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSocialApi(retrofit: Retrofit): SocialApi {
        return retrofit.create(SocialApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppSettingsApi(retrofit: Retrofit): AppSettingsApi {
        return retrofit.create(AppSettingsApi::class.java)
    }
}
