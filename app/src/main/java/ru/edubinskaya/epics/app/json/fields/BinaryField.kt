package ru.edubinskaya.epics.app.json.fields

import android.app.Activity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import gov.aps.jca.CAStatus
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.dbr.ENUM
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.Field

//TODO: difference between set and value
class BinaryField (
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity?
) : Field {
    override var view = GridLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()
    override val fieldName: String?
    override var channel: Channel? = null
    override var monitor: Monitor? = null

    init {
        val epicsListener = EpicsListener.instance
        fieldName = if (jsonRoot.has("name")) jsonRoot.getString("name") else null

        view = activity?.layoutInflater?.inflate(R.layout.boolean_field, null) as GridLayout
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

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            if (event.status === CAStatus.NORMAL && event.dbr is ENUM) {
                val value = (event.dbr as ENUM).enumValue[0]
                activity?.runOnUiThread {
                    view.findViewById<SwitchCompat>(R.id.item_value).isChecked = value != 0.toShort()
                }
            } else {
                activity?.runOnUiThread {
                    view.findViewById<TextView>(R.id.item_value).text = event.status.message
                }
            }
        }
    }
}
