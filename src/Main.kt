import data_retriever.mainDataRetriever
import parser.mainParser
import processor.writeCsvFile
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("log")

fun main() {
    val begin = System.currentTimeMillis()
    val serialData = mainDataRetriever()
    val csvSerialData = mainParser(serialData)
    writeCsvFile(csvSerialData)
    val end = System.currentTimeMillis()
    println("Elapsed time : ${(end-begin)/1000} seconds")
}