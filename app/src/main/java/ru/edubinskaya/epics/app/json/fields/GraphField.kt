package ru.edubinskaya.epics.app.json.fields

import android.app.Activity
import android.graphics.Color
import android.graphics.DashPathEffect
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.event.*
import org.json.JSONObject
import ru.edubinskaya.epics.app.R


class GraphField(
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity?
) : Field {
    override var view = LinearLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()
    override fun blockInput() {}

    override val fieldName: String? = if (jsonRoot.has("name")) jsonRoot.getString("name") else null
    override var channel: Channel? = null
    override var monitor: Monitor? = null
    private val chart: LineChart

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_graph, null) as LinearLayout

        if (fieldName != null) {
            view.findViewById<TextView>(R.id.item_name).text = fieldName
            initializeChannel()
        }

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.layoutDirection
        layoutParams.setMargins(15, 15, 15, 15)
        view.layoutParams = layoutParams

        chart = view.findViewById(R.id.lineChart)

        val values: ArrayList<Entry> = ArrayList()
        for (i in 0..20) {
            val value = (Math.random() * 500).toFloat() - 30
            values.add(Entry(i.toFloat(), value, activity.resources.getDrawable(android.R.drawable.alert_dark_frame)))
        }

        val set1 = LineDataSet(values, "DataSet 1")

        set1.setDrawIcons(false)

        // draw dashed line

        // draw dashed line
        set1.enableDashedLine(10f, 5f, 0f)

        // black lines and points

        // black lines and points
        set1.setColor(Color.BLACK)
        set1.setCircleColor(Color.BLACK)

        // line thickness and point size

        // line thickness and point size
        set1.setLineWidth(1f)
        set1.setCircleRadius(3f)

        // draw points as solid circles

        // draw points as solid circles
        set1.setDrawCircleHole(false)

        // customize legend entry

        // customize legend entry
        set1.setFormLineWidth(1f)
        set1.setFormLineDashEffect(DashPathEffect(floatArrayOf(10f, 5f), 0f))
        set1.setFormSize(15f)

        // text size of values

        // text size of values
        set1.setValueTextSize(9f)

        // draw selection line as dashed

        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f)

        // set the filled area

        // set the filled area
        set1.setDrawFilled(true)
        set1.setFillFormatter(IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum })

        // set color of filled area

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1) // add the data sets


        // create a data object with the data sets

        // create a data object with the data sets
        val data = LineData(dataSets)

        // set data

        // set data
        chart.data = data

        setViewLayoutParams()
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
            }
        }
    }


}
