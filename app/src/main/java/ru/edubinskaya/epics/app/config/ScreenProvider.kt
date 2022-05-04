package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.configurationModel.ContainerType
import ru.edubinskaya.epics.app.configurationModel.ListScreenUnit
import ru.edubinskaya.epics.app.configurationModel.Screen
import ru.edubinskaya.epics.app.configurationModel.ScreenInfo
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader


class ScreenProvider(private val activity: Activity?) {
    val screenList: List<ScreenInfo> get() = getScreens()

    @Throws
    fun getScreenFields(info: ScreenInfo): Screen? {
        if (activity == null) return null
        val jsonRoot = JSONObject(readFile(info.root))

        val jsonArray = jsonRoot.getJSONObject(info.type)
        val mainField = when (jsonArray.getString("type")) {
            ContainerType.LIST.name -> ListScreenUnit(jsonArray, info.pvName, activity)
            else -> ListScreenUnit(JSONObject(""), info.pvName, activity)
        }
        return Screen(info, mainField.view, mainField)
    }

    private fun getScreens(): List<ScreenInfo> {
        if (activity == null) return emptyList()

        val db = activity.openOrCreateDatabase(
            "configuration.db",
            AppCompatActivity.MODE_PRIVATE, null
        )
        db.execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)")

        val query: Cursor = db.rawQuery("SELECT * FROM files;", null)
        val list = ArrayList<ScreenInfo>()

        while (query.moveToNext()) {
            val filename = query.getString(0) + ".json"
            val file = readFile(filename)
            val jsonRoot = JSONObject(file)

            try {
                val jsonArray = jsonRoot.getJSONArray("screens")
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    try {
                        val screen = ScreenInfo(
                            obj.getString("type"),
                            obj.getString("displayed_name"),
                            obj.getString("pv_name"), //TODO: this is really bad
                            filename
                        )
                        list.add(screen)
                    } catch (e: JSONException) {
                        MaterialAlertDialogBuilder(activity)
                            .setTitle("Incorrect config: $filename")
                            .setMessage(e.message + "\n" + obj.toString())
                            .setPositiveButton("OK") { _, _ -> }
                            .show()
                    }
                }
            } catch (e: JSONException) {
                MaterialAlertDialogBuilder(activity)
                    .setTitle("Incorrect config: $filename")
                    .setMessage("Configuration file should contain field \"screens\"")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }
        }

        return list
    }

    fun readFile(filename: String): String {
        try {
            val br = BufferedReader(InputStreamReader(activity?.openFileInput(filename)))
            var line: String? = ""
            val sb = StringBuilder()

            while ((br.readLine().also { line = it }) != null) {
                sb.append(line)
            }
            return sb.toString()
        } catch (e: FileNotFoundException) {
            if (activity != null) {
                MaterialAlertDialogBuilder(activity)
                    .setTitle("File not found")
                    .setMessage("Configuration file $filename.json not found")
                    .setPositiveButton("OK") { _, _ ->
                        val db = activity.openOrCreateDatabase(
                            "configuration.db",
                            AppCompatActivity.MODE_PRIVATE, null
                        )
                        db.execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)")
                        db?.execSQL("DELETE FROM files WHERE (filename = \"$filename\")")
                    }.show()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

}
