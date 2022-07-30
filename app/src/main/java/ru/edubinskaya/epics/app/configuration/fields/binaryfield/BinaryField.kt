package ru.edubinskaya.epics.app.configuration.fields.binaryfield

import android.app.Activity
import android.transition.Visibility
import android.view.View.GONE
import android.view.View.VISIBLE
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
import ru.edubinskaya.epics.app.configuration.fields.Field

class BinaryField(
    private var isActive: Boolean,
    override var jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject?
) : Field(jsonRoot, screenConfig) {
    override var view = LinearLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()
    override lateinit var fieldLabel: String
    private val switch: SwitchCompat
    private val stub: SwitchCompat?

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_boolean_input, null) as LinearLayout

        switch = view.findViewById(R.id.item_value)
        stub = view.findViewById(R.id.stub)

        setDisplayName()
        if (!hasDisplayName) {
            view.findViewById<TextView>(R.id.item_name).text = fieldLabel
        }
        initializeChannel()
        prepareLayout()
    }

    override fun prepareLayout() {
        super.prepareLayout()
        isActive = jsonRoot.getString("type") == "BOOLEAN_INPUT"
        if (isActive) {
            switch.setSwitchTextAppearance(activity, R.style.ColorSwitchStyle_Active)
            switch.visibility = VISIBLE
            stub?.visibility = GONE
            switch.setOnClickListener {
                val value = if (switch.isChecked) 1 else 0
                if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
                    channel?.put(value, BinaryPutListener())
                    Thread() {
                        EpicsContext.context.pendIO(7.0)
                    }.start()
                    switch.setSwitchTextAppearance(activity, R.style.ColorSwitchStyle_Stub)
                } else {
                    switch.isChecked = !switch.isChecked
                }
            }
        } else {
            blockInput()
            switch.visibility = GONE
            stub?.visibility = VISIBLE
            switch.setOnClickListener {}
        }
    }

    override fun blockInput() {
        if (isActive) {
            switch.visibility = GONE
            stub?.visibility = VISIBLE
        }
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
                if (event.status === CAStatus.NORMAL) {
                    if (event.dbr is ENUM) {
                        val value = (event.dbr as ENUM).enumValue[0]
                        view.findViewById<SwitchCompat>(R.id.item_value).isChecked =
                            value != 0.toShort()
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
                if (isActive) {
                    switch.visibility = VISIBLE
                    stub?.visibility = GONE
                }
            }
        }
    }
}
