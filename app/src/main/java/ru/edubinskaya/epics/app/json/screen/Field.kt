package ru.edubinskaya.epics.app.json.screen

import gov.aps.jca.Channel
import gov.aps.jca.Monitor
import gov.aps.jca.event.MonitorListener

interface Field: ScreenUnit {
    val fieldName: String?
    var channel: Channel?
    var monitor: Monitor?
    val monitorListener: MonitorListener

    override fun onDetachView() {
        monitor?.clear()
        channel?.destroy()
    }
}
