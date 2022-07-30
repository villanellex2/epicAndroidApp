package ru.edubinskaya.epics.app.configuration.containers

import android.app.Activity
import android.view.View
import android.widget.*
import androidx.core.widget.NestedScrollView
import org.json.JSONObject
import ru.edubinskaya.epics.app.configuration.ScreenUnit
import kotlin.collections.List

class Table(
    override var jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject?,
) : Container {
    override val children: List<ScreenUnit> = getSubViewList()
    override val view: View

    init {
        if (activity == null) throw IllegalStateException("activity can't be null")
        val columnCount = if (jsonRoot.has("column_count")) {
            jsonRoot.getInt("column_count")
        } else {
            2
        }

        view = if (children.isEmpty()) {
            View(activity)
        } else {

            val grid = GridLayout(activity)
            grid.columnCount = columnCount
            val scrollView = NestedScrollView(activity)
            val horizontalScrollView = HorizontalScrollView(activity)

            scrollView.addView(horizontalScrollView)
            horizontalScrollView.addView(grid)

            children.forEach{ it ->
                grid.addView(it.view)
            }

            scrollView
        }
    }
}
