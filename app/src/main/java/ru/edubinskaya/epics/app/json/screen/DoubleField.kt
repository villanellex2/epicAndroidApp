package ru.edubinskaya.epics.app.json.screen

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import gov.aps.jca.CAStatus
import gov.aps.jca.dbr.DOUBLE
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener

class DoubleField(
    override val fieldName: String,
    override val prefix: String,
    val activity: Activity?
) : Field {
    override var view = GridLayout(activity)
    override var epicsListeners: ArrayList<EpicsListener> = ArrayList<EpicsListener>()
    private val monitor: MonitorListener = DoubleMonitorListener()

    init {
        val epicsListener = EpicsListener()
        epicsListener.execute(this, monitor)
        epicsListeners.add(epicsListener)

        view = activity?.layoutInflater?.inflate(R.layout.field, null) as GridLayout
        view.findViewById<TextView>(R.id.item_name).text = fieldName

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.layoutDirection
        layoutParams.setMargins(15, 15, 15, 15)
        view.layoutParams = layoutParams
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
