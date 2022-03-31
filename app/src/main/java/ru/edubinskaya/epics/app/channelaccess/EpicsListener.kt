package ru.edubinskaya.epics.app.channelaccess

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import org.epics.ca.Channel
import org.epics.ca.Context
import ru.edubinskaya.epics.app.json.Field


class EpicsListener private constructor() {

    companion object {
        @JvmStatic
        val instance = EpicsListener()
    }

    private val context: Context = Context(System.getProperties())

    fun destroy() {
        context.close()
    }

    @SuppressLint("StaticFieldLeak")
    fun execute(field: Field) {
        object : AsyncTask<Any?, Any?, Any?>() {
            override fun doInBackground(objects: Array<Any?>) {
                try {
                    val channel: Channel<Any?> =
                        context.createChannel(field.prefix + ":" + field.fieldName, Any::class.java)
                    channel.connect()
                    field.channel = channel
                    field.monitor = channel.addValueMonitor  { value ->
                        System.out.println(
                            value
                        )
                    }
                } catch (th: Throwable) {
                    th.printStackTrace()
                }
            }
        }.execute()
    }
}