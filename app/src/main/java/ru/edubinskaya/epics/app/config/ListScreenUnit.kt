package ru.edubinskaya.epics.app.config

import android.app.Activity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.json.ContainerType
import ru.edubinskaya.epics.app.json.fields.FieldType
import ru.edubinskaya.epics.app.json.fields.BinaryField
import ru.edubinskaya.epics.app.json.fields.TextField
import ru.edubinskaya.epics.app.json.ScreenUnit
import ru.edubinskaya.epics.app.json.fields.InputTextField

class ListScreenUnit(
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity,
) : ScreenUnit {
    override val view: View
    private val children = getSubViewList()

    init {
        val orientation = if (jsonRoot.has("orientation")) {
            when (jsonRoot.getString("orientation")) {
                "vertical" -> LinearLayoutManager.VERTICAL
                "horizontal" -> LinearLayoutManager.HORIZONTAL
                else -> LinearLayoutManager.VERTICAL
            }
        } else { LinearLayoutManager.VERTICAL }

        if (children.isEmpty()) {
            view = View(activity)
        } else {
            val linearLayout : LinearLayout?
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

    override fun onDetachView() {
        children.forEach{ child -> child.onDetachView()}
    }

    private fun getSubViewList(): List<ScreenUnit> {
        val listOfFields = ArrayList<ScreenUnit>()
        try {
            val jsonArray = jsonRoot.getJSONArray("content")
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val field = when (obj.getString("type")) {
                    FieldType.TEXT_FIELD.name -> TextField(obj ,prefix, activity)
                    FieldType.TEXT_INPUT_NUMBER.name -> InputTextField(obj ,prefix, activity)
                    FieldType.BOOLEAN_INPUT.name -> BinaryField(true, obj, prefix, activity)
                    FieldType.BOOLEAN_FIELD.name -> BinaryField(false, obj, prefix, activity)
                    ContainerType.LIST.name -> ListScreenUnit(obj, prefix, activity)
                    else -> null
                }
                field?.let { listOfFields.add(it) }
            }
        } catch (ignored: JSONException) { }
        return listOfFields
    }
}
