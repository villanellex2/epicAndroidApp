package ru.edubinskaya.epics.app.configurationModel.fields.text

import org.json.JSONObject
import ru.edubinskaya.epics.app.configurationModel.SizeInfo
import ru.edubinskaya.epics.app.configurationModel.SizeInfoType
import ru.edubinskaya.epics.app.configurationModel.fields.FieldInfo

class TextFieldInfo(
    private val pvName: String = "",
    private val displayedName: String? = null,
    private val height: SizeInfo = SizeInfo(SizeInfoType.WRAP_CONTENT),
    private val width: SizeInfo = SizeInfo(SizeInfoType.MATCH_PARENT)
): FieldInfo(pvName, displayedName, height, width) {

    override fun toJson(): JSONObject {
        return super.toJson().apply {
            put("type", "TEXT_FIELD")
        }
    }
}