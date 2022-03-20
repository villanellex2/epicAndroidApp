package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.content.Context
import android.widget.LinearLayout
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.json.ContainerType
import ru.edubinskaya.epics.app.json.screen.Screen
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class ScreenProvider(private val activity: Activity?) {
    val screenList: List<Screen> = getDevices()

    fun getScreenFieldsById(id: String): Screen? {
        if (activity == null) return null
        val jsonRoot = JSONObject(readText(activity, R.raw.list_of_devices))
        var screen: Screen? = null

        for (s in screenList) {
            if (s.id.equals(id)) {
                screen = s
            }
        }

        if (screen == null) return null

        val mainField = try {
            val jsonArray = jsonRoot.getJSONObject(id)
            when (jsonArray.getString("type")) {
                ContainerType.LIST.name -> ListScreenUnit(jsonArray, screen.pvName, activity)
                else ->  ListScreenUnit(JSONObject(""), screen.pvName, activity)
            }
        } catch (e: JSONException) {
            null
        }
        screen.view = mainField?.view!!
        screen.epicsListeners = mainField.epicsListeners
        return screen
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
