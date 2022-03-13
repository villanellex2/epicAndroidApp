package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.json.FieldType
import ru.edubinskaya.epics.app.json.screen.DoubleField
import ru.edubinskaya.epics.app.json.screen.Field
import ru.edubinskaya.epics.app.json.screen.Screen
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class ScreenProvider(private val activity: Activity?) {
    val screenList: List<Screen> = getDevices()

    fun getDeviceFieldsById(id: String): View? {
        if (activity == null) return null
        val jsonRoot = JSONObject(readText(activity, R.raw.list_of_devices))
        val listOfFields = ArrayList<Field>()
        var screen: Screen? = null

        for (s in screenList) {
            if (s.id.equals(id)) {
                screen = s
            }
        }

        if (screen == null) return LinearLayout(activity)
        
        try {
            val jsonArray = jsonRoot.getJSONObject(id)
            return when (jsonArray.getString("type")) {
                "list" -> ListViewParser(jsonArray, screen.pvName, activity).getListView()
                else -> LinearLayout(activity)
            }
        } catch (e: JSONException) {
            return LinearLayout(activity)
        }
    }

    private fun getDevices(): List<Screen> {
        if (activity == null) return emptyList()
        val jsonRoot = JSONObject(readText(activity, R.raw.list_of_devices))
        try {
            val jsonArray = jsonRoot.getJSONArray("devices")
            val listOfDevices = ArrayList<Screen>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val device = Screen(
                    obj.getString("type"),
                    obj.getString("displayed_name"),
                    obj.getString("name"),
                    LinearLayout(activity),
                    emptyList()
                )
                listOfDevices.add(device)
            }
            return listOfDevices
        } catch (e: JSONException) {
            return ArrayList()
        }
    }

    @Throws(IOException::class)
    private fun readText(activity: Context, resId: Int): String {
        val `is`: InputStream = activity.resources.openRawResource(resId)
        val br = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var s: String? = ""
        while (br.readLine().also { s = it } != null) {
            sb.append(s)
            sb.append("\n")
        }
        return sb.toString()
    }

}