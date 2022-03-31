package ru.edubinskaya.epics.app.json.fields

import android.app.Activity
import android.graphics.Color
import android.widget.GridLayout
import android.widget.TextView
import org.epics.ca.Channel
import org.epics.ca.Monitor
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.Field

open class TextField(
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity?
) : Field {
    final override var view = GridLayout(activity)
    override var monitor: Monitor<Any?>? = null
    override fun onValueChanged() {
        TODO("Not yet implemented")
    }

    override var channel: Channel<Any?>? = null
   // val monitorListener = DoubleMonitorListener()
    override val fieldName: String? = if (jsonRoot.has("name")) jsonRoot.getString("name") else null

    init {
        view = activity?.layoutInflater?.inflate(R.layout.text_field, null) as GridLayout
        prepareLayout()
    }

    protected fun prepareLayout() {
        val epicsListener = EpicsListener.instance
        if (fieldName != null) {
            view.findViewById<TextView>(R.id.item_name).text = fieldName
            epicsListener.execute(this)
        }

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.layoutDirection
        layoutParams.setMargins(15, 15, 15, 15)
        view.layoutParams = layoutParams

        setViewLayoutParams()
    }

   /* inner class DoubleMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            if (event.status === CAStatus.NORMAL) {
                val text = when (event.dbr) {
                    is DOUBLE -> (event.dbr as DOUBLE).doubleValue[0].toString()
                    is INT -> (event.dbr as INT).intValue[0].toString()
                    is FLOAT -> (event.dbr as FLOAT).floatValue[0].toString()
                    is SHORT -> (event.dbr as SHORT).shortValue[0].toString()
                    is STRING -> (event.dbr as STRING).stringValue[0].toString()
                    else -> "Incorrect PV type for text field"
                }
                activity?.runOnUiThread {
                    val textView = view.findViewById<TextView>(R.id.item_value)
                    textView.text = text
                    textView.setTextColor(Color.BLACK)
                }
            } else {
                activity?.runOnUiThread {
                    view.findViewById<TextView>(R.id.item_value).text = event.status.message
                }
            }
        }
    }*/
}
