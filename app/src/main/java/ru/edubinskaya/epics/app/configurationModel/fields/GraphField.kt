package ru.edubinskaya.epics.app.configurationModel.fields

import android.app.Activity
import android.util.Log
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
import gov.aps.jca.dbr.*
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R


class GraphField(
    override val jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject
) : Field(jsonRoot, screenConfig) {
    override var view = LinearLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()
    override fun blockInput() {}

    override var fieldLabel: String? = pvName
    private val chart: LineChart
    private val dataSet: LineDataSet

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_graph, null) as LinearLayout

        if (fieldLabel != null) {
            setDisplayName(jsonRoot)
            if (!hasDisplayName) {
                view.findViewById<TextView>(R.id.item_name).text = fieldLabel
            }
            initializeChannel()
        }

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(15, 15, 15, 15)
        view.layoutParams = lp

        chart = view.findViewById(R.id.lineChart)

        val values: ArrayList<Entry> = ArrayList()
        dataSet = if (jsonRoot.has("label")) {
            LineDataSet(values, jsonRoot.getString("label"))
        } else {
            LineDataSet(values, "DataSet 1")
        }

        if (jsonRoot.has("description")) {
            chart.description = Description()
            chart.description.textSize = 14f
            chart.description.text = jsonRoot.getString("description")
        } else {
            chart.description.isEnabled = false
        }

        dataSet.setDrawIcons(false)
        dataSet.color = ContextCompat.getColor(activity, R.color.blue_200)
        dataSet.setCircleColor(ContextCompat.getColor(activity, R.color.blue_200))

        dataSet.lineWidth = 2f
        dataSet.circleRadius = 3.5f

        dataSet.setDrawCircleHole(true)

        dataSet.formLineWidth = 1f
        dataSet.formSize = 15f

        chart.isAutoScaleMinMaxEnabled = true

        chart.setTouchEnabled(true)
        chart.setDragEnabled(true)
        chart.setScaleEnabled(true)
        chart.setPinchZoom(getBooleanInt("pinch_zoom", true))

        dataSet.valueTextSize = 12f

        dataSet.setDrawFilled(getBooleanInt("filled", false))
        dataSet.fillFormatter = IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(dataSet)
        val data = LineData(dataSets)

        chart.data = data
        chart.data.isHighlightEnabled = true

        data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
                if (event.status.isSuccessful) {
                    val dataSet = chart.data.dataSets[0] as LineDataSet
                    val index = dataSet.values.size - 1
                    for (i in 0..index) {
                        dataSet.values.removeFirst()
                    }
                    val point = ContextCompat.getDrawable(activity, android.R.drawable.alert_dark_frame)
                    when (event.dbr.type) {
                        DBRType.DOUBLE -> (event.dbr as DOUBLE).doubleValue.forEachIndexed { index, d ->
                            dataSet.values.add(Entry(index.toFloat(), d.toFloat(), point))
                            chart.data.notifyDataChanged()
                        }
                        DBRType.BYTE -> (event.dbr as BYTE).byteValue.forEachIndexed { index, d ->
                            dataSet.values.add(Entry(index.toFloat(), d.toFloat(), point))
                            chart.data.notifyDataChanged()
                        }
                        DBRType.FLOAT -> (event.dbr as FLOAT).floatValue.forEachIndexed { index, d ->
                            dataSet.values.add(Entry(index.toFloat(), d.toFloat(), point))
                            chart.data.notifyDataChanged()
                        }
                        DBRType.SHORT -> (event.dbr as SHORT).shortValue.forEachIndexed { index, d ->
                            dataSet.values.add(Entry(index.toFloat(), d.toFloat(), point))
                            chart.data.notifyDataChanged()
                        }
                        DBRType.INT -> (event.dbr as INT).intValue.forEachIndexed { index, d ->
                            Log.d("hi?", d.toString())
                            dataSet.values.add(Entry(index.toFloat(), d.toFloat(), point))
                            chart.data.notifyDataChanged()
                        }
                        else -> {
                            setIncorrect(activity)
                        }
                    }

                    activity.runOnUiThread {
                        dataSet.notifyDataSetChanged()
                        chart.data.notifyDataChanged()
                        chart.notifyDataSetChanged()
                        chart.invalidate()
                    }
                }
            }
        }
    }

    private fun getBooleanInt(field: String, default: Boolean): Boolean {
        if (jsonRoot.has(field)) {
            try {
                return (1 == jsonRoot.getInt(field))
            } catch (e: JSONException) { }
        }
        return default
    }
}
