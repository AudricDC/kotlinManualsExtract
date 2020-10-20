package data_retriever

import models.Serial
import logger
import models.CtManualsIndicationsSerial

/**
 * Main function to retrieve data from asMoldedEngineering database.
 * - First retrieves all serials from ctManualsData collection from last nbDays.
 * - Then retrieves data from ctManualsIndications and try to find match in ascData collection.
 *   If one serial does not exist in one of these two collections, it won't be retrieved.
 * Returns a list of Serial objects.
 */
fun mainDataRetriever(): List<Serial> {
    logger.info("[DATA RETRIEVER] - Retrieving data")
    val ctManualsSerials = mainCtManualsRetriever(360)
    val serialData = mutableListOf<Serial>()
    for (ctManualsSerial in ctManualsSerials) {
//        val ctManualsIndicationsSerial = mainCtManualsIndicationRetriever(ctManualsSerial) // no access to data
        val ctManualsIndicationsSerial = CtManualsIndicationsSerial(sn = ctManualsSerial.sn, proximities = emptyList(), horizontalAlignments = emptyList(), verticalAlignments = emptyList())
        val ascSerial = mainAscRetriever(ctManualsSerial)
        if (ctManualsIndicationsSerial != null && ascSerial != null) {
            val serial = Serial(sn = ctManualsSerial.sn, isAlleviated = ctManualsSerial.isAlleviated,
                    tomoPlant = ctManualsSerial.plant, annotations = ctManualsSerial.annotations,
                    ascPlant = ascSerial.plant, operations = ascSerial.operations,
                    proximities = ctManualsIndicationsSerial.proximities,
                    verticalAlignments = ctManualsIndicationsSerial.verticalAlignments,
                    horizontalAlignments = ctManualsIndicationsSerial.horizontalAlignments)
            serialData.add(serial)
        }
    }
    logger.info("[DATA RETRIEVER] - ${serialData.size} serials retrieved from both 3 databases")
    return serialData
}

fun main() {
    val serialData = mainDataRetriever()
    println(serialData.toString())
}