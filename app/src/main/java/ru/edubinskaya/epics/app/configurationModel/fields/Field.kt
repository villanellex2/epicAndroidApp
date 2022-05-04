package ru.edubinskaya.epics.app.configurationModel.fields

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import gov.aps.jca.CAStatus
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.dbr.DBR
import gov.aps.jca.dbr.STRING
import gov.aps.jca.event.GetListener
import gov.aps.jca.event.MonitorListener
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsContext
import ru.edubinskaya.epics.app.configurationModel.ScreenUnit

interface Field: ScreenUnit {
    var fieldLable: String?
    var hasDisplayName: Boolean
    var channel: Channel?
    var monitor: Monitor?
    val monitorListener: MonitorListener
    var descChannel: Channel?

    //TODO: disconnect listener

    override fun onDetachView() {
        Thread {
            monitor?.clear()
            channel?.destroy()
            descChannel?.destroy()
            channel = null
            monitor = null
        }.start()
    }

    fun setDisplayName(jsonRoot: JSONObject) {
        if (jsonRoot.has("displayed_name")) {
            try {
                setLabel(jsonRoot.getString("displayed_name"))
                hasDisplayName = true
            } catch (e: JSONException) {}
        }
    }

    fun setIncorrectPvType(activity: Activity?) {
        setIncorrect(activity)
        Toast.makeText(activity, "Incorrect PV type for field $fieldLable", Toast.LENGTH_LONG)
            .show()
        onDetachView()
    }

    fun setConnected(activity: Activity?) {
        view.background = activity?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.device_field_background
            )
        }
    }

    fun setDisconnected(activity: Activity?) {
        view.background = activity?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.device_field_background_no_connection
            )
        }
        activity?.runOnUiThread {
            blockInput()
        }
    }

    fun setIncorrect(activity: Activity?) {
        view.background = activity?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.device_field_background_icorrect_type
            )
        }
    }

    fun blockInput()

    fun setLabel(label: String) {
        if (label.isNotEmpty()) {
            fieldLable = label
            view.findViewById<TextView>(R.id.item_name).text = fieldLable
        }
    }

    override fun createMonitor() {
        if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
            monitor = channel?.addMonitor(Monitor.VALUE, monitorListener)
        } else {
            setDisconnected(activity)
        }
    }

    @SuppressLint("StaticFieldLeak")
    fun initializeChannel() {
        object : AsyncTask<Any?, Any?, DBR?>() {
            override fun doInBackground(objects: Array<Any?>): DBR? {
                try {
                    //TODO: more prefix
                    channel = EpicsContext.context.createChannel("$prefix:$fieldLable")
                    descChannel = EpicsContext.context.createChannel("$prefix:$fieldLable.DESC")
                } catch (th: Throwable) { }
                return null
            }
        }.execute()
    }

    override fun onChannelCreated() {
        if (!hasDisplayName && channel?.connectionState == Channel.ConnectionState.CONNECTED) {
            if (descChannel?.connectionState == Channel.ConnectionState.CONNECTED) {
                Thread {
                    descChannel?.get(GetListener { ev ->
                        if (ev?.status == CAStatus.NORMAL && ev?.dbr is STRING) {
                            val value = (ev.dbr as? STRING)?.stringValue
                            if (value?.size == 1) {
                                setLabel(value[0])
                            }
                        }
                    })
                }.start()
            }
        }
    }
}
