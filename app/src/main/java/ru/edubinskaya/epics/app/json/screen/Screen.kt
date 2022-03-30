package ru.edubinskaya.epics.app.json.screen

import android.view.View

class Screen (
    val id: Int,
    val type: String?,
    val displayedName: String?,
    val pvName: String,
    var view: View,
    var epicsListeners: List<EpicsListener>
)