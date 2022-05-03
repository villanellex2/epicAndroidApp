package ru.edubinskaya.epics.app.configurationModel

import android.view.View

class Screen (
    val id: Int,
    val type: String?,
    val displayedName: String?,
    val pvName: String,
    var view: View,
    var mainField: ScreenUnit?
)