package ru.edubinskaya.epics.app.json

import org.epics.ca.Channel
import org.epics.ca.Monitor

interface Field: ScreenUnit {
    val fieldName: String?
    var channel: Channel<Any?>?
    var monitor: Monitor<Any?>?

    fun onValueChanged()

    override fun onDetachView() {
        monitor?.close()
        channel?.close()
    }
}
