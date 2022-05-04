package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.database.Cursor
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.configurationModel.ContainerType
import ru.edubinskaya.epics.app.configurationModel.ListScreenUnit
import ru.edubinskaya.epics.app.configurationModel.Screen
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader


class ScreenProvider(private val activity: Activity?) {
    val screenList: List<Screen> = getScreens()

    @Throws
    fun getScreenFields(screen: Screen): Screen? {
        if (activity == null) return null
        val jsonRoot = JSONObject(readFile(screen.root))

        val jsonArray = jsonRoot.getJSONObject(screen.type)
        val mainField = when (jsonArray.getString("type")) {
            ContainerType.LIST.name -> ListScreenUnit(jsonArray, screen.pvName, activity)
            else -> ListScreenUnit(JSONObject(""), screen.pvName, activity)
        }
        screen.view = mainField?.view!!
        screen.mainField = mainField
        return screen
    }

    private fun getScreens(): List<Screen> {
        if (activity == null) return emptyList()

        val db = activity.openOrCreateDatabase(
            "configuration.db",
            AppCompatActivity.MODE_PRIVATE, null
        )
        db.execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)")

        val query: Cursor = db.rawQuery("SELECT * FROM files;", null)
        val list = ArrayList<Screen>()

        while (query.moveToNext()) {
            val filename = query.getString(0) + ".json"
            try {
                val file = readFile(filename)
                val jsonRoot = JSONObject(file)
                val jsonArray = jsonRoot.getJSONArray("devices")

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val device = Screen(
                        i,
                        obj.getString("type"),
                        obj.getString("displayed_name"),
                        obj.getString("name"),
                        LinearLayout(activity),
                        null,
                        filename
                    )
                    list.add(device)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
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
            e.printStackTrace()
            //TODO: maybe delete from db? show alert
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

}
