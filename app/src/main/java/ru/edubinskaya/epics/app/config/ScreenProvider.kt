package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.configurationModel.containers.ContainerType
import ru.edubinskaya.epics.app.configurationModel.containers.list.List
import ru.edubinskaya.epics.app.configurationModel.Screen
import ru.edubinskaya.epics.app.configurationModel.ScreenInfo
import ru.edubinskaya.epics.app.configurationModel.containers.Table
import ru.edubinskaya.epics.app.configurationModel.fields.*
import ru.edubinskaya.epics.app.configurationModel.fields.text.InputTextField
import ru.edubinskaya.epics.app.configurationModel.fields.text.TextField
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader


class ScreenProvider(private val activity: Activity?) {
    val screenList: kotlin.collections.List<ScreenInfo> get() = getScreens()

    @Throws
    fun getScreenFields(info: ScreenInfo): Screen? {
        if (activity == null) return null
        val jsonRoot = JSONObject(readFile(info.root))

        val jsonArray = jsonRoot.getJSONObject("templates").getJSONObject(info.type)
        val type = jsonArray.getString("type")

        val field = when (type) {
            FieldType.TEXT_FIELD.name -> TextField(jsonArray, activity, JSONObject(info.jsonObject))
            FieldType.TEXT_INPUT_NUMBER.name -> InputTextField(jsonArray, activity, JSONObject(info.jsonObject))
            FieldType.GRAPH.name -> GraphField(jsonArray, activity, JSONObject(info.jsonObject))
            FieldType.BOOLEAN_INPUT.name -> BinaryField(true, jsonArray, activity, JSONObject(info.jsonObject))
            FieldType.BOOLEAN_FIELD.name -> BinaryField(false, jsonArray, activity, JSONObject(info.jsonObject))
            ContainerType.LIST.name -> List(jsonArray, activity, JSONObject(info.jsonObject))
            ContainerType.TABLE.name -> Table(jsonArray, activity, JSONObject(info.jsonObject))
            else -> null
        } ?: throw JSONException("Incorrect field type: $type in $jsonArray")
        return Screen(info, field.view, field)
    }

    private fun getScreens(): kotlin.collections.List<ScreenInfo> {
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
            try {
                val jsonRoot = JSONObject(file)
                val jsonArray = jsonRoot.getJSONArray("screens")
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    try {
                        val screen = ScreenInfo(
                            obj.getString("type"),
                            obj.getString("displayed_name"),
                            filename,
                            obj.toString()
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
                    .setMessage(e.message)
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
