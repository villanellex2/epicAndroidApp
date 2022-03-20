package ru.edubinskaya.epics.app.json.screen

import android.view.View
import ru.edubinskaya.epics.app.channelaccess.EpicsListener

class Screen (
    val id: String?,
    val displayedName: String?,
    val pvName: String,
    var view: View,
    var epicsListeners: List<EpicsListener>
)