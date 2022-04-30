package ru.edubinskaya.epics.app.json.fields

import android.app.Activity
import android.graphics.Color
import android.graphics.DashPathEffect
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.dbr.STRING
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
        for (i in 0..40) {
            val value = (Math.random() * 500).toFloat() - 30
            values.add(Entry(i.toFloat()+55, value, ContextCompat.getDrawable(activity, android.R.drawable.alert_dark_frame)))
        }

        val set1 = if (jsonRoot.has("label")) {
            LineDataSet(values, jsonRoot.getString("label")) }
        else {
            LineDataSet(values, "DataSet 1") }

        if (jsonRoot.has("description")) {
            chart.description = Description()
            chart.description.textSize = 14f
            chart.description.text = jsonRoot.getString("description")
        } else {
            chart.description.isEnabled = false
        }
        set1.setDrawIcons(false)
        set1.color = Color.BLACK
        set1.setCircleColor(Color.BLACK)

        set1.lineWidth = 1f
        set1.circleRadius = 3f

        set1.setDrawCircleHole(false)

        set1.formLineWidth = 1f
        set1.formSize = 15f

        chart.isDragEnabled = true
        chart.isAutoScaleMinMaxEnabled = true
        chart.setScaleEnabled(true)
        set1.valueTextSize = 12f

        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)

        val data = LineData(dataSets)

        chart.data = data

        setViewLayoutParams()
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
                if (event.status.isSuccessful) {
                    val dbr = event.dbr as STRING
                    val isDoubles = event.dbr.isDOUBLE
                    val isFloat = event.dbr.printInfo()

                    val value = dbr.stringValue[0]

                    value
                }
            }
        }
    }


}
