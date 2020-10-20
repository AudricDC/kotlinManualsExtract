package data_retriever

import com.mongodb.BasicDBObject
import com.mongodb.client.FindIterable
import extractors.MongoDBInformation
import org.bson.BsonDocument
import org.bson.BsonElement
import org.bson.BsonInt32
import org.bson.Document
import safran.sae.ix.MongoLocator
import logger
import models.*
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
 * Function to query one serial in ctManualsIndications collection.
 */
private fun queryCtManualsIndicationsSerial(serial: CtManualsSerial): FindIterable<Document> {
//    logger.info("[CT MANUALS INDICATIONS RETRIEVER] - Querying one serial")
    val query = BasicDBObject()
    query.put("sn", serial.sn)

    return mgdb.find(query).projection(BsonDocument(listOf(
            BsonElement("sn", BsonInt32(1)),
            BsonElement("proximities", BsonInt32(1)),
            BsonElement("horizontalAlignments", BsonInt32(1)),
            BsonElement("verticalAlignments", BsonInt32(1))
    )))
}

/**
 * Function to parse a  mongo document into a CtManualsIndicationSerial object.
 */
private fun parseCtManualsIndicationsSerial(sn: String, documents: FindIterable<Document>): CtManualsIndicationsSerial? {
//    logger.info("[CT MANUALS INDICATIONS RETRIEVER] - Parsing one serial")
    if (documents.toList().isNullOrEmpty()) return CtManualsIndicationsSerial(sn = sn, proximities = listOf(),
            horizontalAlignments = listOf(), verticalAlignments = listOf())
    val document = documents.toList()[0]
    return try {
        mongoAccessGson.toObject(document, CtManualsIndicationsSerial::class.java)
    } catch (e: Exception) {
        logger.warn("[CT MANUALS INDICATIONS RETRIEVER] - Parsing Error ${document["sn"]} : $e")
        null
    }
}

/**
 * Main function to query serial from ctManualsIndications and parse it into an object.
 */
fun mainCtManualsIndicationRetriever(serial: CtManualsSerial) : CtManualsIndicationsSerial? {
    val documents = queryCtManualsIndicationsSerial(serial)
    return parseCtManualsIndicationsSerial(serial.sn, documents)
}

fun main() {
    val serialList = mainCtManualsRetriever(10)
    for (serial in serialList) {
        val ctManualsIndicationsSerial = mainCtManualsIndicationRetriever(serial)
        println(ctManualsIndicationsSerial?.toString() ?: "No match found in ctManualsIndications")
    }
}
