package ru.edubinskaya.epics.app.configuration.fields.mbbi

import android.app.Activity
import android.view.View
import org.json.JSONObject
import ru.edubinskaya.epics.app.configuration.SizeInfo
import ru.edubinskaya.epics.app.configuration.SizeInfoType
import ru.edubinskaya.epics.app.configuration.fields.FieldInfo

class MbbiFieldInfo(
    private val pvName: String = "",
    private val displayedName: String? = null,
    private val height: SizeInfo = SizeInfo(SizeInfoType.WRAP_CONTENT),
    private val width: SizeInfo = SizeInfo(SizeInfoType.MATCH_PARENT)
): FieldInfo(pvName, displayedName, height, width) {

    override fun toJson(): JSONObject {
        return super.toJson().apply {
            put("type", "MBBI")
        }
    }

    override fun getListOfParamSetters(activity: Activity, onUpdate: () -> Unit): ArrayList<View> {
        return super.getListOfParamSetters(activity, onUpdate).apply {

        }
    }
}