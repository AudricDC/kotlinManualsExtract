package data_retriever

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.client.FindIterable
import extractors.DateRange
import extractors.MongoDBInformation
import org.bson.BsonDocument
import org.bson.BsonElement
import org.bson.BsonInt32
import org.bson.Document
import safran.sae.ix.MongoLocator
import logger
import models.AscSerial
import models.CtManualsSerial
import java.lang.Exception

/**
 * All information for mongodb connection.
 */
private val dbInfo = MongoDBInformation(
        "localhost",
        27017,
        "user",
        "pwd",
        "db",
        "collection"
)

private val mongoAccessGson = MongoLocator.getMongoGsonClient(dbInfo.uri)

private val mgdb = mongoAccessGson.getCollection(dbInfo.database, dbInfo.collection)

/**
 * Function to query one serial in ascData collection.
 */
private fun queryAscSerial(serial: CtManualsSerial): FindIterable<Document> {
//    logger.info("[ASC RETRIEVER] - Querying one serial")
    val query = BasicDBObject()
    query.put("sn", serial.sn)
    return mgdb.find(query).projection(BsonDocument(listOf(
            BsonElement("sn", BsonInt32(1)),
            BsonElement("plant", BsonInt32(1)),
            BsonElement("operations", BsonInt32(1))
    )))
}

/**
 * Function to parse a  mongo document into an AscSerial object.
 */
private fun parseAscSerial(documents: FindIterable<Document>): AscSerial? {
//    logger.info("[ASC RETRIEVER] - Parsing one serial")

    if (documents.toList().isNullOrEmpty()) return null
    val document = documents.toList()[0]
    return try {
        mongoAccessGson.toObject(document, AscSerial::class.java)
    } catch (e: Exception) {
        logger.warn("[BLADE SPC VALUES] - Parsing Error ${document["sn"]} : $e")
        null
    }
}

/**
 * Main function to query serial from ascData and parse it into an object.
 */
fun mainAscRetriever(serial: CtManualsSerial): AscSerial? {
    val documents = queryAscSerial(serial)
    return parseAscSerial(documents)
}

fun main() {
    val serialList = mainCtManualsRetriever(180)
    for (serial in serialList) {
        val ascSerial = mainAscRetriever(serial)
        println(ascSerial?.toString() ?: "No match found in ascData")
    }
}
