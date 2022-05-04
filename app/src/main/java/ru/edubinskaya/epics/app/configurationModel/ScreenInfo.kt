package ru.edubinskaya.epics.app.configurationModel

import java.io.Serializable

class ScreenInfo (
    val type: String?,
    val displayedName: String?,
    val pvName: String,
    val root: String
) : Serializable