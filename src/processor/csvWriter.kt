package processor

import data_retriever.mainDataRetriever
import extractors.OutputInformation
import models.CsvSerial
import java.io.BufferedWriter
import java.io.FileWriter
import logger
import parser.mainParser
import java.util.Collections.list

fun outputInfo() = OutputInformation(
        "path_to\\output_path",
        "powerBI_extract",
        ".csv"
)

/**
 * Function to write a list of CsvSerial into a csv.
 */
fun writeCsvFile(csvSerials: List<CsvSerial>) {
    val outInfo = outputInfo()

    logger.info("[CSV WRITER] - Writing CSV file to ${outInfo.file}")
    val writer = BufferedWriter(FileWriter(outInfo.file.toString()))


    val header = listOf("qty", "serial", "isAlleviated", "tomoPlant", "ascPlant", "loomLJ", "dateLJ",
            "nbOfProximities", "nbOfVerticalAlignments", "nbOfHorizontalAlignment")
    // Write header
    writer.write(header.joinToString(";"))

    for (csvSerial in csvSerials) {
        writer.newLine()
        val valueList = listOf(1, csvSerial.sn, csvSerial.isAlleviated.toString(),
                csvSerial.tomoPlant, csvSerial.ascPlant, csvSerial.loomLJ, csvSerial.dateLJ.toLocalDate(),
                csvSerial.nbOfProximities, csvSerial.nbOfVAlignments, csvSerial.nbOfHAlignment)
        writer.write(valueList.joinToString(";"))
    }
    writer.close()
}


fun main() {
    val serialData = mainDataRetriever()
    val csvSerialData = mainParser(serialData)
    writeCsvFile(csvSerialData)
}