package ru.edubinskaya.epics.app.json.screen

import android.view.View
import ru.edubinskaya.epics.app.channelaccess.EpicsListener

interface ScreenUnit {
    val prefix: String
    val view: View
    var epicsListeners: ArrayList<EpicsListener>
}
