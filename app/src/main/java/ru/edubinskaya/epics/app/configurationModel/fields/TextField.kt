package ru.edubinskaya.epics.app.configurationModel.fields

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

open class TextField (
    final override val jsonRoot: JSONObject,
    override val prefix: String,
    final override val activity: Activity?
) : Field() {

    final override var view = GridLayout(activity)
    override val monitorListener = DoubleMonitorListener()
    override fun blockInput() {}

    override val pvName: String? = jsonRoot.getString("name")
    final override var fieldLabel: String? = jsonRoot.getString("name")

    init {
        view = activity?.layoutInflater?.inflate(R.layout.text_field, null) as GridLayout
        prepareLayout()
    }

    fun prepareLayout() {
        if (fieldLabel != null) {
            setDisplayName(jsonRoot)
            if (!hasDisplayName) {
                view.findViewById<TextView>(R.id.item_name).text = fieldLabel
            }
            initializeChannel()
        }

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.layoutDirection
        layoutParams.setMargins(15, 15, 15, 15)
        view.layoutParams = layoutParams

        setViewLayoutParams()
    }

    inner class DoubleMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            if (event.status === CAStatus.NORMAL) {
                val text = event.dbr.asString()
                activity?.runOnUiThread {
                    if (text != null) {
                        view.findViewById<TextView>(R.id.item_value).text = text
                        setConnected(activity)
                        view.invalidate()
                    } else {
                        setIncorrectPvType(activity)
                    }
                }
            } else {
                activity?.runOnUiThread {
                    view.findViewById<TextView>(R.id.item_value).text = event.status.message
                    setIncorrect(activity)
                }
            }
        }
    }

    fun DBR.asString(): String? {
        return when (this) {
            is DOUBLE -> (this as DOUBLE).doubleValue[0].toString()
            is INT -> (this as INT).intValue[0].toString()
            is FLOAT -> (this as FLOAT).floatValue[0].toString()
            is SHORT -> (this as SHORT).shortValue[0].toString()
            is STRING -> (this as STRING).stringValue[0].toString()
            else -> null
        }
    }
}
