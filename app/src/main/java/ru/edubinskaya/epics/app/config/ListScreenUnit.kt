package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.text.Layout
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.ContainerType
import ru.edubinskaya.epics.app.json.FieldType
import ru.edubinskaya.epics.app.json.screen.DoubleField
import ru.edubinskaya.epics.app.json.screen.ScreenUnit

class ListScreenUnit(
    private val jsonRoot: JSONObject,
    override val prefix: String,
    val activity: Activity,
) : ScreenUnit {
    override val view: View
    override var epicsListeners: ArrayList<EpicsListener> = ArrayList()

    init {
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

        if (listOfFields.isEmpty()) {
            view = View(activity)
        } else {
            var linearLayout : LinearLayout? = null
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                val scrollView = HorizontalScrollView(activity)
                linearLayout = LinearLayout(activity)
                scrollView.addView(linearLayout)
                view = scrollView
            } else {
                val scrollView = activity.layoutInflater.inflate(R.layout.scroll_view, null)
                linearLayout = scrollView.findViewById(R.id.main_view) as LinearLayout
                view = scrollView
            }

            linearLayout.orientation = orientation
            for (child in listOfFields) {
                linearLayout.addView(child.view)
            }
            for (field in listOfFields) {
                epicsListeners.addAll(field.epicsListeners)
            }
        }
    }

    fun getSubViewList(): List<ScreenUnit> {
        val listOfFields = ArrayList<ScreenUnit>()
        try {
            val jsonArray = jsonRoot.getJSONArray("content")
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val field = when (obj.getString("type")) {
                    FieldType.DOUBLE_VALUE.name -> DoubleField(
                        obj.getString("name"),
                        prefix,
                        activity
                    )
                    ContainerType.LIST.name -> ListScreenUnit(obj, prefix, activity)
                    else -> null
                }
                field?.let { listOfFields.add(it) }
            }
        } catch (ignored: JSONException) {
        }
        return listOfFields
    }
}