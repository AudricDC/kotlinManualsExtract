package data_retriever

import com.mongodb.BasicDBObject
import com.mongodb.BasicDBObjectBuilder
import com.mongodb.client.FindIterable
import extractors.DateRange
import extractors.MongoDBInformation
import org.bson.Document
import safran.sae.ix.MongoLocator
import logger
import models.CtManualsSerial
import org.bson.BsonDocument
import org.bson.BsonElement
import org.bson.BsonInt32
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
 * Function to query all serials, from ctManualsData, within a date range.
 */
private fun queryCtManualsSerials(nbDays: Long): FindIterable<Document> {
//    logger.info("[CT MANUALS RETRIEVER] - Querying serials")
    val dateRange = DateRange(nbDays)
    val query = BasicDBObject()
    query["acquisitionDate"] = BasicDBObjectBuilder.start("\$gte", dateRange.start).add("\$lte", dateRange.stop).get()
//    query["annotations.type"] = BasicDBObjectBuilder.start("\$in", annotationList).get()
    return mgdb.find(query).projection(BsonDocument(listOf(
            BsonElement("sn", BsonInt32(1)),
            BsonElement("isAlleviated", BsonInt32(1)),
            BsonElement("plant", BsonInt32(1)),
            BsonElement("annotations", BsonInt32(1))
    )))
}

/**
 * Function to parse a  mongo document into a CtManualsSerial object.
 */
private fun parseCtManualsSerials(documents: FindIterable<Document>): List<CtManualsSerial> {
//    logger.info("[CT MANUALS RETRIEVER] - Parsing serials")
    val dataList = mutableListOf<CtManualsSerial>()
    for (doc in documents) {
        try {
            dataList.add(mongoAccessGson.toObject(doc, CtManualsSerial::class.java))
        } catch (e: Exception) {
            logger.warn("[CT MANUALS RETRIEVER] - Parsing Error ${doc["sn"]} : $e")
        }
    }
    logger.info("[CT MANUALS RETRIEVER] - ${dataList.size} serials retrieved from ctManualsData")
    return dataList
}

/**
 * Main function to query serial from ctManualsData and parse it into an object.
 */
fun mainCtManualsRetriever(nbDays: Long): List<CtManualsSerial> {
    val documents = queryCtManualsSerials(nbDays)
    return parseCtManualsSerials(documents)
}

fun main() {
    val serialList = mainCtManualsRetriever(10)
    println(serialList.take(1))
}