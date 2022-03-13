package ru.edubinskaya.epics.app.json.screen

import android.view.View

class Screen (
    val id: String?,
    val displayedName: String?,
    val pvName: String,
    val view: View,
    val listOfFields: List<Field>
)