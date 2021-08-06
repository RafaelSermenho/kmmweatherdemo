package dev.rafaelsermenho.kmmweathershared.api

import dev.rafaelsermenho.kmmweathershared.entity.Response
import io.ktor.client.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

class OpenWeatherAPI {
    companion object {
        private const val OPEN_WEATHER_ENDPOINT = "\n" +
                "https://api.openweathermap.org/data/2.5/weather?appid=YOUR_API_KEY&units=metric&lang=pt_br"
    }

    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun getWeatherForCity(city: String = "Campinas"): Response {
        return httpClient.get("$OPEN_WEATHER_ENDPOINT&q=$city")
    }
}
