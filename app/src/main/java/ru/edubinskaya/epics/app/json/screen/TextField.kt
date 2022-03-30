package ru.edubinskaya.epics.app.json.screen

import android.app.Activity
import android.widget.GridLayout
import android.widget.TextView
import gov.aps.jca.CAStatus
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.dbr.*
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener

class TextField (
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity?
) : Field {
    override var view = GridLayout(activity)
    override var monitor: Monitor? = null
    override var channel: Channel? = null
    override val monitorListener = DoubleMonitorListener()
    override val fieldName: String?

    init {
        val epicsListener = EpicsListener.instance
        fieldName = if (jsonRoot.has("name")) jsonRoot.getString("name") else null

        view = activity?.layoutInflater?.inflate(R.layout.double_field, null) as GridLayout
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

    inner class DoubleMonitorListener() : MonitorListener {
        private var incorrectTryCount = 0

        override fun monitorChanged(event: MonitorEvent) {
            if (event.status === CAStatus.NORMAL) {
                incorrectTryCount = 0
                val text = when (event.dbr) {
                    is DOUBLE -> (event.dbr as DOUBLE).doubleValue[0].toString()
                    is INT -> (event.dbr as INT).intValue[0].toString()
                    is FLOAT -> (event.dbr as FLOAT).floatValue[0].toString()
                    is SHORT -> (event.dbr as SHORT).shortValue[0].toString()
                    is STRING -> (event.dbr as STRING).stringValue[0].toString()
                    else -> "Incorrect PV type for text field"
                }
                activity?.runOnUiThread { view.findViewById<TextView>(R.id.item_value).text = text }
            } else {
                incorrectTryCount++
                if (incorrectTryCount >= 5) {
                    activity?.runOnUiThread { view.findViewById<TextView>(R.id.item_value).text = event.status.message }
                }
            }
        }
    }
}
