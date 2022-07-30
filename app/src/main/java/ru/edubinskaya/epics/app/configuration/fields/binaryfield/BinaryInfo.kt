package ru.edubinskaya.epics.app.configuration.fields.binaryfield

import android.app.Activity
import android.view.View
import org.json.JSONObject
import ru.edubinskaya.epics.app.configuration.SizeInfo
import ru.edubinskaya.epics.app.configuration.SizeInfoType
import ru.edubinskaya.epics.app.configuration.fields.FieldInfo

class BinaryInfo(
    pvName: String = "",
    displayedName: String? = null,
    height: SizeInfo = SizeInfo(SizeInfoType.WRAP_CONTENT),
    width: SizeInfo = SizeInfo(SizeInfoType.MATCH_PARENT)
): FieldInfo(pvName, displayedName, height, width) {

    private var isActive = false

    override fun toJson(): JSONObject {
        return super.toJson().apply {
            if (isActive) {
                put("type", "BOOLEAN_INPUT")
            } else {
                put("type", "BOOLEAN_FIELD")
            }
        }
    }

    override fun getListOfParamSetters(activity: Activity, onUpdate: () -> Unit): ArrayList<View> {
        return super.getListOfParamSetters(activity, onUpdate).apply {
            add(0, createCheckParamSetter("Can change state", activity, onUpdate) {
                isActive = it
                onUpdate.invoke()
            })
        }
    }
}