package ru.edubinskaya.epics.app.json.screen

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import gov.aps.jca.CAStatus
import gov.aps.jca.dbr.DOUBLE
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.FieldType

class DoubleField(override val fieldName: String, override val prefix: String, val activity: Activity?) : Field {
    override val fieldType = FieldType.DOUBLE_VALUE
    override val view = TextView(activity)
    override val monitor: MonitorListener = DoubleMonitorListener()
    override var epicsListener: EpicsListener? = null

    init {
        epicsListener = EpicsListener()
        epicsListener?.execute(this, monitor)

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.setGravity(Gravity.CENTER_VERTICAL or Gravity.END)
        layoutParams.layoutDirection
        view.layoutParams = layoutParams
    }

    inner class DoubleMonitorListener() : MonitorListener {
        private var incorrectTryCount = 0
        /**
         * @see MonitorListener.monitorChanged
         */
        override fun monitorChanged(event: MonitorEvent) {
            if (event.status === CAStatus.NORMAL) {
                incorrectTryCount = 0
                val text = (event.dbr as DOUBLE).doubleValue[0].toString()
                activity?.runOnUiThread {view.text = text}
            } else {
                incorrectTryCount++
                if (incorrectTryCount >= 5) {
                    activity?.runOnUiThread {view.text = event.status.message}
                }
            }
        }
    }
}