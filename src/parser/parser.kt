package parser

import data_retriever.mainDataRetriever
import models.CsvSerial
import models.Serial
import java.lang.Exception
import logger

/**
 * Parses one Serial object to a CsvSerial object.
 */
fun parseOneSerial(serial: Serial): CsvSerial {
    return CsvSerial(sn = serial.sn, isAlleviated = serial.getAlleviatedBoolean(), tomoPlant = serial.tomoPlant,
            annotationsDict = serial.getAllAnnotationsCount(),
            ascPlant = serial.ascPlant, loomLJ = serial.getLoomLJ(), dateLJ = serial.getDateLJ(),
            nbOfProximities = serial.getNbOfProximities(), nbOfHAlignment = serial.getNbOfHAlignments(),
            nbOfVAlignments = serial.getNbOfVAlignments())
}

/**
 * Main function to parse a list of serial object, to a list of CsvSerial object.
 * Takes a list of Serial in parameter and returns a list of CsvSerial.
 */
fun mainParser(serialData: List<Serial>): List<CsvSerial> {
    logger.info("[PARSER] - Parsing serials to csvSerials")
    val csvSerialData = mutableListOf<CsvSerial>()
    for (serial in serialData) {
        try {
            csvSerialData.add(parseOneSerial(serial))
        } catch (e: Exception) {
            logger.warn("Failed to parse serial ${serial.sn}")
        }
    }
    logger.info("[DATA RETRIEVER] - ${csvSerialData.size} serials retrieved with known loomLJ")
    return csvSerialData
}

fun main() {
    val serialData = mainDataRetriever()
    val csvSerialData = mainParser(serialData)
    print(csvSerialData.toString())
}