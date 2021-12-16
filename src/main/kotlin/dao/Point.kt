package dao

import kotlinx.serialization.Serializable

@Serializable
class Point(val coordinates:DoubleArray) {
    val type = "Point"
}