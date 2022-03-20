package ru.edubinskaya.epics.app.json.screen

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import gov.aps.jca.CAStatus
import gov.aps.jca.dbr.DOUBLE
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener

class DoubleField(
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity?,
) : Field {
    override var view = GridLayout(activity)
    override var epicsListeners: ArrayList<EpicsListener> = ArrayList<EpicsListener>()
    private val monitor: MonitorListener = DoubleMonitorListener()
    override val fieldName: String?

    init {
        val epicsListener = EpicsListener()
        fieldName = if (jsonRoot.has("name")) jsonRoot.getString("name") else null

        view = activity?.layoutInflater?.inflate(R.layout.field, null) as GridLayout
        if (fieldName != null) {
            view.findViewById<TextView>(R.id.item_name).text = fieldName
            epicsListener.execute(this, monitor)
            epicsListeners.add(epicsListener)
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
                val text = (event.dbr as DOUBLE).doubleValue[0].toString()
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
