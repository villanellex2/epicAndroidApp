package ru.edubinskaya.epics.app.configurationModel.containers

import android.app.Activity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.configurationModel.ScreenUnit
import ru.edubinskaya.epics.app.configurationModel.fields.*
import kotlin.collections.List

class List(
    override val jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject,
) : Container {
    override val children: List<ScreenUnit> = getSubViewList()
    override val view: View

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
                val scrollView = activity?.layoutInflater?.inflate(R.layout.scroll_view, null)
                linearLayout = scrollView?.findViewById(R.id.main_view) as LinearLayout
                view = scrollView
            }

            linearLayout.orientation = orientation
            for (child in children) {
                linearLayout.addView(child.view)
            }
        }
    }
}
