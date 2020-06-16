package com.example.sharedcode.di

import com.example.sharedcode.network.BASE_URL
import com.example.sharedcode.network.NewsApi
import com.example.sharedcode.network.NewsApiImpl
import com.example.sharedcode.repo.NewsListRepositoryImpl
import com.example.sharedcode.repo.NewsRepository
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.host
import kotlinx.serialization.json.JsonConfiguration
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
val kodein = Kodein {
    bind<HttpClient>() with singleton {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json(
                        JsonConfiguration(
                            encodeDefaults = false,
                            allowStructuredMapKeys = true,
                            ignoreUnknownKeys = true,
                            isLenient = true
                        )
                    )
                ).apply {
                    useDefaultTransformers = true
                }

                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }
        }
    }

    bind<NewsApi>() with singleton {
        NewsApiImpl(instance())
    }

    bind<NewsRepository>() with singleton {
        NewsListRepositoryImpl(instance())
    }
}