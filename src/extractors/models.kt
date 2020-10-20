package extractors

import java.io.File
import java.time.LocalDateTime

class OutputInformation(
        val path: String,
        val filename: String,
        val extension: String,
        val file: File = File(path, filename + extension)
)

class MongoDBInformation(
        val host: String,
        val port: Int,
        val user: String,
        val pwd: String,
        val database: String,
        val collection: String,
        val uri: String = "mongodb://$user:$pwd@$host:$port/admin"
)

class DateRange(nbDays: Long) {
    val stop = LocalDateTime.now()
    val start = stop.minusDays(nbDays)
}