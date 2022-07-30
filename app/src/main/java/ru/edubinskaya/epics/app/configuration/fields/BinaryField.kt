package ru.edubinskaya.epics.app.configuration.fields

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import gov.aps.jca.CAStatus
import gov.aps.jca.Channel
import gov.aps.jca.dbr.ENUM
import gov.aps.jca.dbr.SHORT
import gov.aps.jca.event.*
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsContext

class BinaryField(
    private val isActive: Boolean,
    override var jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject?
) : Field(jsonRoot, screenConfig) {
    override var view = LinearLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()

    override fun blockInput() {
        if (isActive) {
            stub?.visibility = View.VISIBLE
            switch.visibility = View.GONE
        }
    }

    override var fieldLabel: String = pvName

    private val switch: SwitchCompat
    private val stub: SwitchCompat?

    init {
        view = if (isActive) {
            activity?.layoutInflater?.inflate(R.layout.boolean_input, null) as LinearLayout
        } else {
            activity?.layoutInflater?.inflate(R.layout.boolean_field, null) as LinearLayout
        }

        switch = view.findViewById(R.id.item_value)
        stub = view.findViewById(R.id.stub)

        if (!isActive) {
            switch.isClickable = false
        }

        if (fieldLabel != null) {
            setDisplayName()
            if (!hasDisplayName) {
                view.findViewById<TextView>(R.id.item_name).text = fieldLabel
            }
            initializeChannel()
        }

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(15, 15, 15, 15)
        view.layoutParams = lp

        setViewLayoutParams()

        switch.setOnClickListener {
            if (!isActive) return@setOnClickListener
            val value = if (switch.isChecked) 1 else 0
            if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
                channel?.put(value, BinaryPutListener())
                Thread() {
                    EpicsContext.context.pendIO(7.0)
                }.start()
                stub.isChecked = switch.isChecked
                stub.visibility = View.VISIBLE
                switch.visibility = View.GONE
            } else {
                switch.isChecked = !switch.isChecked
            }
        }
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
                if (event.status === CAStatus.NORMAL) {
                    if (event.dbr is ENUM) {
                        val value = (event.dbr as ENUM).enumValue[0]
                        view.findViewById<SwitchCompat>(R.id.item_value).isChecked = value != 0.toShort()
                        setConnected(activity)
                    } else {
                        setIncorrectPvType(activity)
                    }
                } else {
                    view.findViewById<TextView>(R.id.item_value).text = event.status.message
                    setIncorrect(activity)
                }
            }
        }
    }

    inner class BinaryPutListener() : PutListener {
        override fun putCompleted(ev: PutEvent?) {
            if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
                //TODO: очередь действий на Connected
                channel?.get(InputBinaryGetListener(ev))
                EpicsContext.context.pendIO(7000.0)
            }
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
                        val value = when (event.dbr) {
                            is ENUM -> (event.dbr as ENUM).enumValue[0]
                            is SHORT -> (event.dbr as SHORT).shortValue[0]
                            else -> false
                        }
                        if (value != false) {
                            switch.isChecked = value == 1.toShort()
                            stub?.isChecked = value == 1.toShort()
                            setConnected(activity)
                        } else {
                            setIncorrectPvType(activity)
                        }
                    } else {
                        setDisconnected(activity)
                    }
                } else {
                    Toast.makeText(activity, event?.status?.message, Toast.LENGTH_LONG).show()
                    setIncorrect(activity)
                }
                switch.visibility = View.VISIBLE
                stub?.visibility = View.GONE
            }
        }
    }
}
