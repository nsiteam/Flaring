package dao

import LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


@Serializable
data class Gfs(
    val id: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val temp: Double,
    val specHumidity: Double,
    val relHumidity: Double,
    val uWind: Double,
    val vWind: Double
)
