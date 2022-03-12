package ru.edubinskaya.epics.app.config

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.model.Device
import ru.edubinskaya.epics.app.model.DeviceField
import ru.edubinskaya.epics.app.model.FieldType
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class DeviceListProvider(private val context: Context?) {
    val deviceList: List<Device> = getDevices()

    fun getDeviceFieldsByType(type: String): List<DeviceField> {
        val jsonRoot = JSONObject(context?.let { readText(it, R.raw.list_of_devices) })
        val listOfFields = ArrayList<DeviceField>()
        try {
            val jsonArray = jsonRoot.getJSONArray(type)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val field = DeviceField(
                    obj.getString("name"),
                    FieldType.valueOf(obj.getString("type"))
                )
                listOfFields.add(field)
            }
            return listOfFields
        } catch (e: JSONException) {
            return ArrayList()
        }
    }

    private fun getDevices(): List<Device> {
        if ( context == null) return ArrayList()
        val jsonRoot = JSONObject(readText(context, R.raw.list_of_devices))
        try {
            val jsonArray = jsonRoot.getJSONArray("devices")
            val listOfDevices = ArrayList<Device>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val device = Device(
                    obj.getString("type"),
                    obj.getString("displayed_name"),
                    obj.getString("name")
                )
                listOfDevices.add(device)
            }
            return listOfDevices
        } catch (e: JSONException) {
            return ArrayList()
        }
    }

    @Throws(IOException::class)
    private fun readText(context: Context, resId: Int): String {
        val `is`: InputStream = context.getResources().openRawResource(resId)
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