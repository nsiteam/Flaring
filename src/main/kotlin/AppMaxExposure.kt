import com.mongodb.client.model.Filters
import dao.Gfs
import dao.VnfEvent
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.litote.kmongo.*
import org.locationtech.jts.geom.Coordinate
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow


// This calculates worst case contaminant levels 1km from the source in the direction of
// the wind.

fun cWorstCase(q: Double, wind: Double): Double {
    return 1.0e9 * q / (wind * 50.0 * 100.0 * 1000.0)
}

data class Contaminants(
    val date: LocalDateTime,
    val rh: Double,
    val methane: Double,
    val lat: Double,
    val lon: Double,
    val co2: Double,
    val co: Double,
    val no2: Double,
    val no_l: Double,
    val no_h: Double,
    val h2s: Double,
    val so2: Double
)

data class LocationDate(val d: LocalDate, val lat: Double, val lon: Double)

fun main() {
    val CM_Per_MW = 38279.0  // Cubic meters of methane for one megawatt
    val client = KMongo.createClient()
    val db = client.getDatabase("flare")
    val colVnf = db.getCollection<VnfEvent>()
    val colGfs = db.getCollection<Gfs>()
    val crs = CRS.decode("EPSG:4326")

    val contaminants =
        colVnf.find().filter { (it.date.year == 2020) && it.lat1k < 75.0 && it.lat1k > -55.0 }
            .mapNotNull { flare ->
                val distance = LocationData.sites.map{JTS.orthodromicDistance(Coordinate(flare.lat1k,flare.lon1k),Coordinate(it.lat,it.lon),crs)}
                val index = distance.indexOf(distance.minOrNull())

                val location = LocationData.sites[index]

                val weather = colGfs.findOne(Filters.eq("id", flare.id))
                if (weather != null) {
                    val windSpeed = (weather.uWind.pow(2.0) + weather.vWind.pow(2.0)).pow(.5)
                    val methaneM3 = flare.rhPrime * CM_Per_MW

                    val qCO2 = location.co2 * methaneM3
                    val qCO = location.co * methaneM3
                    val qNO_l = location.no_low * methaneM3
                    val qNO_h = location.no_high * methaneM3
                    val qNO2 = location.no2 * methaneM3
                    val qH2S = location.h2s * methaneM3
                    val qSO2 = location.so2 * methaneM3

                    val SECONDS_PER_DAY = 86400.0  // Assume uniform emission over the day

                    val cwcCO2 = cWorstCase(qCO2 / SECONDS_PER_DAY, windSpeed)
                    val cwcCO = cWorstCase(qCO / SECONDS_PER_DAY, windSpeed)
                    val cwcNOL = cWorstCase(qNO_l / SECONDS_PER_DAY, windSpeed)
                    val cwcNOH = cWorstCase(qNO_h / SECONDS_PER_DAY, windSpeed)
                    val cwcNO2 = cWorstCase(qNO2 / SECONDS_PER_DAY, windSpeed)
                    val cwcH2S = cWorstCase(qH2S / SECONDS_PER_DAY, windSpeed)
                    val cwcSO2 = cWorstCase(qSO2 / SECONDS_PER_DAY, windSpeed)

                    Contaminants(
                        flare.date,
                        flare.rhPrime,
                        methaneM3 / 1.0e6,
                        flare.lat1k,
                        flare.lon1k,
                        cwcCO2,
                        cwcCO,
                        cwcNO2,
                        cwcNOL,
                        cwcNOH,
                        cwcH2S,
                        cwcSO2
                    )
                } else null
            }

    val groupedByLocationDate = contaminants.groupBy { LocationDate(it.date.toLocalDate(), it.lat, it.lon) }

    val aggregatedByLocation = groupedByLocationDate.map { (k, v) ->
        if (v.size == 1) v.first()
        else {
            Contaminants(v.first().date, v.sumOf { it.rh }, v.sumOf { it.methane }, v.first().lat, v.first().lon,
                v.sumOf { it.co2 }, v.sumOf { it.co }, v.sumOf { it.no2 }, v.sumOf { it.no_l },v.sumOf { it.no_h },
                v.sumOf { it.h2s },v.sumOf { it.so2 })
        }
    }

    val out = File("data/flareExposure.csv").bufferedWriter()

    out.write("date,rh,methane(million m^3),lat,lon,CO2(mg/m^3),CO(mg/m^3)," +
            "NO2(mg/m^3),NO (low temp)(mg/m^3),NO (high temp)(mg/m^3),H2S(mg/m^3),SO2(mg/m^3)\n")

    // Dump Top 1000 CO2 Levels
    aggregatedByLocation.sortedByDescending { it.methane }.forEach {
        out.write("${it.date.format(DateTimeFormatter.BASIC_ISO_DATE)},${it.rh},${it.methane},${it.lat},${it.lon},${it.co2},${it.co},${it.no2}," +
                "${it.no_l},${it.no_h},${it.h2s},${it.so2}\n")
    }
    out.flush()
    out.close()
}