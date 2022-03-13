package ru.edubinskaya.epics.app.config

import DeviceInfoRecyclerViewAdapter
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.FieldType
import ru.edubinskaya.epics.app.json.screen.DoubleField
import ru.edubinskaya.epics.app.json.screen.Field

class ListViewParser(
    private val jsonRoot: JSONObject,
    private val pvName: String,
    val activity: Activity
) {
    val listeners = ArrayList<EpicsListener>()

    fun getListView(): View {
        val orientation = try {
            when (jsonRoot.getString("orientation")) {
                "vertical" -> LinearLayoutManager.VERTICAL
                "horizontal" -> LinearLayoutManager.HORIZONTAL
                else -> LinearLayoutManager.VERTICAL
            }
        } catch (e: JSONException) {
            LinearLayout.VERTICAL
        }

        val listOfFields = getSubViewList()

        if (listOfFields.isEmpty()) return View(activity)
        val recyclerView = RecyclerView(activity)
        recyclerView.layoutManager = LinearLayoutManager(activity, orientation, false)
        recyclerView.adapter = DeviceInfoRecyclerViewAdapter(activity, listOfFields)

        for (field in listOfFields) {
            field.epicsListener?.let { listeners.add(it) }
        }
        return recyclerView
    }

    fun getSubViewList(): List<Field> {
        val listOfFields = ArrayList<Field>()
        try {
            val jsonArray = jsonRoot.getJSONArray("content")
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val field = when (FieldType.valueOf(obj.getString("type"))) {
                    FieldType.DOUBLE_VALUE -> DoubleField(
                        obj.getString("name"),
                        pvName,
                        activity
                    )
                    else -> null
                }
                field?.let { listOfFields.add(it) }
            }
        } catch (ignored: JSONException) {
        }
        return listOfFields
    }
}