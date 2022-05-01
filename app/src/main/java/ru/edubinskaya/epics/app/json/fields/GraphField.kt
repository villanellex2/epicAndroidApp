package ru.edubinskaya.epics.app.json.fields

import android.app.Activity
import android.graphics.Color
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
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.dbr.*
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
    override var descChannel: Channel? = null
    override var monitor: Monitor? = null
    private val chart: LineChart
    private val dataSet: LineDataSet

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
        dataSet = if (jsonRoot.has("label")) {
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
        dataSet.setDrawIcons(false)
        dataSet.color = Color.BLACK
        dataSet.setCircleColor(Color.BLACK)

        dataSet.lineWidth = 1f
        dataSet.circleRadius = 3f

        dataSet.setDrawCircleHole(false)

        dataSet.formLineWidth = 1f
        dataSet.formSize = 15f

        chart.isDragEnabled = true
        chart.isAutoScaleMinMaxEnabled = true
        chart.setScaleEnabled(true)
        dataSet.valueTextSize = 12f

        dataSet.setDrawFilled(true)
        dataSet.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(dataSet)

        val data = LineData(dataSets)

        chart.data = data

        setViewLayoutParams()
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
                if (event.status.isSuccessful) {
                    val set = ArrayList<Entry>()
                    val point = ContextCompat.getDrawable(activity, android.R.drawable.alert_dark_frame)
                    when (event.dbr.type) {
                        DBRType.DOUBLE -> (event.dbr as DOUBLE).doubleValue.forEachIndexed { index, d ->  set.add(Entry(index.toFloat(), d.toFloat(), point))}
                        DBRType.BYTE -> (event.dbr as BYTE).byteValue.forEachIndexed { index, d ->  set.add(Entry(index.toFloat(), d.toFloat(), point))}
                        DBRType.FLOAT -> (event.dbr as FLOAT).floatValue.forEachIndexed { index, d ->  set.add(Entry(index.toFloat(), d.toFloat(), point))}
                        DBRType.SHORT -> (event.dbr as SHORT).shortValue.forEachIndexed { index, d ->  set.add(Entry(index.toFloat(), d.toFloat(), point))}
                        DBRType.INT -> (event.dbr as INT).intValue.forEachIndexed { index, d ->  set.add(Entry(index, d.toFloat(), point))}
                    }
                    dataSet.values = set
                    dataSet.notifyDataSetChanged()
                }
            }
        }
    }
}
