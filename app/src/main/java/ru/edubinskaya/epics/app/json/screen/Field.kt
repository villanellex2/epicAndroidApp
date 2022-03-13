package ru.edubinskaya.epics.app.json.screen

import android.view.View
import gov.aps.jca.event.MonitorListener
import ru.edubinskaya.epics.app.channelaccess.EpicsListener
import ru.edubinskaya.epics.app.json.FieldType

interface Field {
    val fieldName: String
    val fieldType: FieldType
    val prefix: String
    val monitor: MonitorListener
    val view: View
    var epicsListener: EpicsListener?
}
