package ru.edubinskaya.epics.app.configurationModel

import org.json.JSONObject
import java.io.Serializable

class ScreenInfo (
    val type: String?,
    val displayedName: String?,
    val root: String,
    val jsonObject: JSONObject
) : Serializable