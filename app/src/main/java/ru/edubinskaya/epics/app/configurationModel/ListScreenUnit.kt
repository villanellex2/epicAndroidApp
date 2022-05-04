package ru.edubinskaya.epics.app.configurationModel

import android.app.Activity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.configurationModel.fields.*

class ListScreenUnit(
    override val jsonRoot: JSONObject,
    override val activity: Activity,
    override val screenConfig: JSONObject,
) : ScreenUnit {
    override val view: View
    val children = getSubViewList()

    init {
        val orientation = if (jsonRoot.has("orientation")) {
            when (jsonRoot.getString("orientation")) {
                "vertical" -> LinearLayoutManager.VERTICAL
                "horizontal" -> LinearLayoutManager.HORIZONTAL
                else -> LinearLayoutManager.VERTICAL
            }
        } else {
            LinearLayoutManager.VERTICAL
        }

        if (children.isEmpty()) {
            view = View(activity)
        } else {
            val linearLayout: LinearLayout?
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
            for (child in children) {
                linearLayout.addView(child.view)
            }
        }
    }

    override fun onDetachView() = children.forEach { child -> child.onDetachView() }

    override fun createMonitor() = children.forEach { child -> child.createMonitor() }

    override fun onChannelCreated()  = children.forEach { child -> child.onChannelCreated()}

    private fun getSubViewList(): List<ScreenUnit> {
        val listOfFields = ArrayList<ScreenUnit>()
        try {
            val jsonArray = jsonRoot.getJSONArray("content")
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                try {
                    val field = when (obj.getString("type")) {
                        FieldType.TEXT_FIELD.name -> TextField(obj, activity, screenConfig)
                        FieldType.TEXT_INPUT_NUMBER.name -> InputTextField(obj, activity, screenConfig)
                        FieldType.GRAPH.name -> GraphField(obj, activity, screenConfig)
                        FieldType.BOOLEAN_INPUT.name -> BinaryField(true, obj, activity, screenConfig)
                        FieldType.BOOLEAN_FIELD.name -> BinaryField(false, obj, activity, screenConfig)
                        ContainerType.LIST.name -> ListScreenUnit(obj, activity, screenConfig)
                        else -> null
                    }
                    field?.let { listOfFields.add(it) }
                } catch (e: JSONException) {
                    AlertDialog.Builder(activity)
                        .setMessage(e.message + " in \n" + obj.toString())
                        .setTitle("Incorrect json. Field skipped.")
                        .setNegativeButton("OK") { _, _ -> }
                        .show()

                }
            }
        } catch (ignored: JSONException) {
        }
        return listOfFields
    }
}
