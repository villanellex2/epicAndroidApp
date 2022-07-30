package ru.edubinskaya.epics.app.configuration

import org.json.JSONObject

interface ScreenItemsInfo {
    fun toJson(): JSONObject
}

