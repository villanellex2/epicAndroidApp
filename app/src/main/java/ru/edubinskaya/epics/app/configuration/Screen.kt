package ru.edubinskaya.epics.app.configuration

import android.view.View

class Screen(
    private val info: ScreenInfo,
    var view: View,
    var mainField: ScreenUnit?
) {
    val type get() = info.type
    val displayedName get() = info.displayedName
    val root get() = info.root
}