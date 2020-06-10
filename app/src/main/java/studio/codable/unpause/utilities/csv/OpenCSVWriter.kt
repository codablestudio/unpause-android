package studio.codable.unpause.utils.openCSV

import android.content.Context
import android.os.Environment
import com.opencsv.CSVWriter
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.utilities.manager.TimeManager
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer

class OpenCSVWriter {
    companion object{
        fun writeShiftsToCSV(context: Context?, shifts: List<Shift>?, name: String): File? {
            var csvFile: File? = null
            try {
                csvFile = createCSVFile(context, name)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (csvFile != null) {
                saveFileToPhone(context!!, csvFile, shifts)
            }
            return csvFile
        }

        @Throws(IOException::class)
        private fun createCSVFile(context: Context?, name: String): File {
            val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            if (storageDir != null) {
                for (file in storageDir.listFiles())
                    if (!file.isDirectory)
                        file.delete()
            }
            val pdfFile =
                File.createTempFile(context?.getString(R.string.csv_file_name_2nd_part, name), ".csv", storageDir)
            pdfFile.deleteOnExit()
            return pdfFile
        }

        private fun saveFileToPhone(context: Context, path: File, shifts: List<Shift>?) {
            try {
                val writer = CSVWriter(FileWriter(path) as Writer?)
                val data = ArrayList<Array<String>>()
                data.add(
                    arrayOf(
                        context.getString(R.string.arrivedAt),
                        context.getString(R.string.leftAt),
                        context.getString(R.string.description),
                        context.getString(R.string.hours)
                    )
                )
                var totalHours = 0.0
                if (shifts != null) {
                    for (shift in shifts){
                        val timeManager = TimeManager(shift.arrivalTime!!, shift.exitTime!!)
                        data.add(arrayOf(
                            "${timeManager.arrivalToArray()[1]} ${timeManager.arrivalToArray()[0]}",
                            "${timeManager.exitToArray()[1]} ${timeManager.exitToArray()[0]}",
                            shift.description.toString(),
                            timeManager.getWorkingHoursDecimal().toString()))
                        totalHours += TimeManager(shift.arrivalTime!!, shift.exitTime!!).getWorkingHoursDecimal()
                    }
                }
                data.add(arrayOf(""))
                data.add(arrayOf(context.getString(R.string.total_hours), totalHours.toString()))
                writer.writeAll(data)
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}