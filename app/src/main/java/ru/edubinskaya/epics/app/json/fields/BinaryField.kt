package ru.edubinskaya.epics.app.json.fields

import android.app.Activity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import gov.aps.jca.CAStatus
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.dbr.ENUM
import gov.aps.jca.dbr.SHORT
import gov.aps.jca.event.*
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.Field

class BinaryField (
    private val isActive: Boolean,
    override val jsonRoot: JSONObject,
    override val prefix: String,
    override val activity: Activity?
) : Field {
    override var view = GridLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()
    override val fieldName: String?
    override var channel: Channel? = null
    override var monitor: Monitor? = null

    private val switch: SwitchCompat
    private val stub: SwitchCompat?

    init {
        val epicsListener = EpicsListener.instance
        fieldName = if (jsonRoot.has("name")) jsonRoot.getString("name") else null

        view = if (isActive) {
            activity?.layoutInflater?.inflate(R.layout.boolean_input, null) as GridLayout
        } else {
            activity?.layoutInflater?.inflate(R.layout.boolean_field, null) as GridLayout
        }

        switch = view.findViewById(R.id.item_value)
        stub = view.findViewById(R.id.stub)

        if (fieldName != null) {
            view.findViewById<TextView>(R.id.item_name).text = fieldName
            epicsListener.execute(this)
        }

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.layoutDirection
        layoutParams.setMargins(15, 15, 15, 15)
        view.layoutParams = layoutParams

        setViewLayoutParams()

        switch.setOnClickListener {
            if (!isActive) return@setOnClickListener
            val value = if (switch.isChecked) 1 else 0

            channel?.put(value, BinaryPutListener())
            Thread() {
                EpicsListener.context.pendIO(7000.0)
            }.start()
            stub.isChecked = switch.isChecked
            stub.visibility = View.VISIBLE
            switch.visibility = View.GONE
        }
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

    inner class BinaryPutListener() : PutListener {
        override fun putCompleted(ev: PutEvent?) {
            channel?.get(InputBinaryGetListener(ev))
            EpicsListener.context.pendIO(7000.0)
        }
    }

    inner class InputBinaryGetListener(val ev: PutEvent?) : GetListener {
        override fun getCompleted(event: GetEvent?) {
            activity?.runOnUiThread {
                if (CAStatus.NORMAL == event?.status) {
                    if (ev?.status != CAStatus.NORMAL) {
                        Toast.makeText(activity, ev?.status?.message, Toast.LENGTH_LONG).show()
                    }
                    if (event != null) {
                        val value = when(event.dbr) {
                            is ENUM -> (event.dbr as ENUM).enumValue[0]
                            is SHORT -> (event.dbr as SHORT).shortValue[0]
                            else -> false //todo: aalsllalsllsal
                        }
                        switch.isChecked = value == 1.toShort()
                        stub?.isChecked = value == 1.toShort()
                    } else {
                        //todo : show must go on
                    }
                } else {
                    Toast.makeText(activity, event?.status?.message, Toast.LENGTH_LONG).show()
                    //todo: show must go on
                }
                switch.visibility = View.VISIBLE
                stub?.visibility = View.GONE
            }
        }
    }
}
