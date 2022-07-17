package ru.edubinskaya.epics.app.configurationModel.containers.list

import org.json.JSONArray
import org.json.JSONObject
import ru.edubinskaya.epics.app.configurationModel.SizeInfo
import ru.edubinskaya.epics.app.configurationModel.fields.FieldInfo
import java.util.*
import kotlin.collections.ArrayList

class ListInfo(
    private val orientation: ListOrientation,
    private val height: SizeInfo,
    private val width: SizeInfo,
    private val items: ArrayList<JSONObject> = ArrayList(),
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("orientation", orientation.name.lowercase(Locale.getDefault()))
            put("height", height.toString())
            put("width", width.toString())
            val itemsJson = JSONArray()
            items.forEach { itemsJson.put(it)  }
            put("content", itemsJson)
            put("type", "LIST")
        }
    }

    fun addItem(item: FieldInfo) {
        items.add(item.toJson())
    }

    fun addItem(item: JSONObject) {
        items.add(item)
    }
}

enum class ListOrientation {
    VERTICAL,
    HORIZONTAL
}