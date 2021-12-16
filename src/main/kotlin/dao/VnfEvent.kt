package dao

import LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.math.pow

@Serializable
data class VnfEvent(
    val id: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val date: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val localDate:LocalDateTime,
    val esfBB:Double,
    val tempBB: Double,
    val tempBkg: Double,
    val rhi: Double,
    val rh: Double,
    val rhPrime:Double,
    val area: Double,
    val areaBB:Double,
    val cloudMask:Int,
    val qfFit:Int,
    val qfDetect:Int,
    val lat: Double,
    val lon: Double,
    val lat1k:Double,
    val lon1k:Double
) {
    val _id = id
    val location =  Point(doubleArrayOf(lon,lat))

    fun rhPrime(e:Double):Double{
        return this.rh*this.area*this.area.pow(e)
    }
}
