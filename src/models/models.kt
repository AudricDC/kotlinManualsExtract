package models

import org.nield.kotlinstatistics.countBy
import java.lang.Exception
import java.time.LocalDateTime

data class TestObject(val msg:String, val myList: List<Int>)

data class Proximity(val xCenter: Double, val yCenter: Double)

data class Alignment(val coordinate: Double)

data class CtManualsIndicationsSerial(val sn: String, val proximities: List<Proximity>,
                                      val horizontalAlignments: List<Alignment>, val verticalAlignments: List<Alignment>)

data class Operation(val workCenter: String, val date: LocalDateTime)

data class AscSerial(val sn: String, val plant: String, val operations: List<Operation>)


data class Annotation(val type: String)

data class CtManualsSerial(val sn: String, val isAlleviated: String, val plant: String, var annotations: List<Annotation>)

data class Serial(val sn: String, val isAlleviated: String, val tomoPlant: String, val annotations: List<Annotation>,
                  val ascPlant: String, val operations: List<Operation>, val proximities: List<Proximity>,
                  val verticalAlignments: List<Alignment>, val horizontalAlignments: List<Alignment>) {
    fun getAlleviatedBoolean() = isAlleviated.toUpperCase() == "YES"
    fun getAllAnnotationsCount() = annotations.map { it.type }.countBy()
    //    fun getAllAnnotationsCount() = annotations.map { it.type }.countBy().values.toList()
    fun getLoomLJ(): String {
        return try {
            operations.filter { it.workCenter.contains("LJ") }.toList()[0].workCenter
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getDateLJ(): LocalDateTime {
        return try {
            operations.filter { it.workCenter.contains("LJ") }.toList()[0].date
        } catch (e: Exception) {
            LocalDateTime.MIN
        }
    }

    fun getNbOfProximities() = proximities.size
    fun getNbOfVAlignments() = verticalAlignments.size
    fun getNbOfHAlignments() = horizontalAlignments.size

}

data class CsvSerial(val sn: String, val isAlleviated: Boolean, val tomoPlant: String, val annotationsDict: Map<String, Int>,
                     val ascPlant: String, val loomLJ: String, val dateLJ: LocalDateTime,
                     val nbOfProximities: Int, val nbOfVAlignments: Int, val nbOfHAlignment: Int)