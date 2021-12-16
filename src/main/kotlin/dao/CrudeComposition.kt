package dao

data class CrudeComposition(
    val name:String,
    val reactionType:String,
    val lat:Double,
    val lon:Double,
    val co2:Double,
    val co:Double,
    val h2s:Double,
    val no_low:Double,
    val no_high:Double,
    val no2:Double,
    val so2:Double
) {
    val location =  Point(doubleArrayOf(lon,lat))
}
