import com.mongodb.client.model.Filters
import dao.VnfEvent
import org.geotools.data.DataStoreFinder
import org.geotools.data.DataUtilities
import org.litote.kmongo.*
import org.locationtech.jts.geom.MultiPolygon
import java.io.File

// Here is an example of using the VNF lat/lon data and a shapefile of country boundaries to
// label events by country

fun main() {
    val client = KMongo.createClient()
    val db = client.getDatabase("flaring")
    val col = db.getCollection<VnfEvent>()

    val input = File("data/shp/ne_110m_admin_0_countries.shp")
    val map = hashMapOf("url" to input.toURI().toURL())
    val datastore = DataStoreFinder.getDataStore(map)
    val source = datastore.getFeatureSource(datastore.typeNames[0])

    val shps = DataUtilities.list(source.features)

    shps.forEach { country ->

        val name = country.getAttribute("ADMIN").toString().uppercase()

        val geom = country.defaultGeometry as? MultiPolygon

        val points = geom?.coordinates?.map {
            listOf(it.x, it.y)
        }?.toMutableList() ?: mutableListOf()

        val flaresInCountry = col.find(Filters.geoWithinPolygon("location", points)).toList()

        val rhTotal = flaresInCountry.sumOf{it.rhPrime}

        println("$name : $rhTotal")
    }
}
