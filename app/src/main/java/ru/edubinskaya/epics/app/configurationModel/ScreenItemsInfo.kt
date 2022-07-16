package ru.edubinskaya.epics.app.configurationModel

import org.json.JSONObject

interface ScreenItemsInfo {
    fun toJson(): JSONObject
}

