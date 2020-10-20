package data_retriever

import com.mongodb.BasicDBObject
import com.mongodb.client.FindIterable
import extractors.MongoDBInformation
import org.bson.BsonDocument
import org.bson.BsonElement
import org.bson.BsonInt32
import org.bson.Document
import safran.sae.ix.MongoLocator
import java.lang.Exception
import logger
import models.TestObject

private val dbInfo = MongoDBInformation("localhost", 27017, "admin", "admin", "testdb", "testcoll")

private val mongoAccessGson = MongoLocator.getMongoGsonClient(dbInfo.uri)

private val mgdb = mongoAccessGson.getCollection(dbInfo.database, dbInfo.collection)

/**
 * Function to query one serial in ascData collection.
 */
private fun queryTestObject(): FindIterable<Document> {
//    logger.info("[ASC RETRIEVER] - Querying one serial")
    val query = BasicDBObject()
    return mgdb.find(query).projection(BsonDocument(listOf(
            BsonElement("msg", BsonInt32(1)),
            BsonElement("myList", BsonInt32(1))
    )))
}

/**
 * Function to parse a  mongo document into an AscSerial object.
 */
private fun parseTestObject(documents: FindIterable<Document>): TestObject? {
//    logger.info("[ASC RETRIEVER] - Parsing one serial")

    if (documents.toList().isNullOrEmpty()) {
        logger.error("No docs")
        return null }
    val document = documents.toList()[0]
    return try {
        mongoAccessGson.toObject(document, TestObject::class.java)
    } catch (e: Exception) {
        logger.warn("[BLADE SPC VALUES] - Parsing Error ${document["sn"]} : $e")
        null
    }
}

/**
 * Main function to query serial from ascData and parse it into an object.
 */
fun mainTestObjectRetriever(): TestObject? {
    val documents = queryTestObject()
    return parseTestObject(documents)
}

fun main() {
    val testObjects = mainTestObjectRetriever()
    println(testObjects.toString())
}