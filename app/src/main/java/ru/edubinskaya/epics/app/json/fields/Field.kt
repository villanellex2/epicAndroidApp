package ru.edubinskaya.epics.app.json.fields

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.widget.Toast
import androidx.core.content.ContextCompat
import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.TimeoutException
import gov.aps.jca.dbr.DBR
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsContext
import ru.edubinskaya.epics.app.json.ScreenUnit

interface Field: ScreenUnit {
    val fieldName: String?
    var channel: Channel?
    var monitor: Monitor?
    val monitorListener: MonitorListener
    var descChannel: Channel?

    //TODO: disconnect listener
    //TODO: desc to name and field name

    override fun onDetachView() {
        Thread {
            monitor?.clear()
            channel?.destroy()
            channel = null
            monitor = null
        }.start()
    }

    fun setIncorrectPvType(activity: Activity?) {
        setIncorrect(activity)
        Toast.makeText(activity, "Incorrect PV type for field $fieldName", Toast.LENGTH_LONG)
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

    override fun createMonitor() {
        if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
            monitor = channel?.addMonitor(Monitor.VALUE, monitorListener)
        } else {
            setDisconnected(activity)
        }
        if (descChannel?.connectionState == Channel.ConnectionState.CONNECTED) {
            monitor = descChannel?.addMonitor(Monitor.VALUE, descListener())
        }
    }

    @SuppressLint("StaticFieldLeak")
    fun initializeChannel() {
        //TODO: check if needs to
        object : AsyncTask<Any?, Any?, DBR?>() {
            override fun doInBackground(objects: Array<Any?>): DBR? {
                try {
                    val channel = EpicsContext.context.createChannel(prefix + ":" + fieldName)
                    descChannel = EpicsContext.context.createChannel(prefix + ":" + fieldName + ".DESC")
                    this@Field.channel = channel
                } catch (th: Throwable) { }
                return null
            }
        }.execute()
    }

    fun descListener(): MonitorListener = object: MonitorListener {
        override fun monitorChanged(ev: MonitorEvent?) {
            ev?.dbr
        }
    }
}
