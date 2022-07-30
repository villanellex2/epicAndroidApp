package ru.edubinskaya.epics.app.configuration.fields

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject
import ru.edubinskaya.epics.app.configuration.ScreenItemsInfo
import ru.edubinskaya.epics.app.configuration.SizeInfo
import ru.edubinskaya.epics.app.configuration.SizeInfoType

abstract class FieldInfo(
    private var pvName: String,
    private var displayedName: String?,
    private val height: SizeInfo,
    private val width: SizeInfo
): ScreenItemsInfo {

    override fun toJson(): JSONObject {
        return JSONObject().apply {
            put("height", height.toString())
            put("width", width.toString())
            put("pv_name", pvName)
            put("displayed_name", displayedName)
            displayedName?.let { put("displayed_name", displayedName) }
        }
    }


    open fun getListOfParamSetters(activity: Activity, onUpdate: () -> Unit): ArrayList<View> {
        val list = ArrayList<View>()

        list.add(createStringParamSetter("pv name", activity, onUpdate) { newText: String -> pvName = newText })
        list.add(createStringParamSetter("displayed name", activity, onUpdate) { newText: String -> displayedName = newText })
        list.add(height.getParamSetter("height", activity, SizeInfoType.WRAP_CONTENT, onUpdate))
        list.add(width.getParamSetter("width", activity, SizeInfoType.MATCH_PARENT, onUpdate))

        return list
    }

    protected fun createStringParamSetter(title: String, activity: Activity, onUpdate: () -> Unit, onChangeListener: TextChangeListener): View {
        val view = LinearLayout(activity).apply {
            addView(TextView(activity).apply { text = title })
            addView(EditText(activity).apply {
                addTextChangedListener(object :
                    TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        onChangeListener.onTextChanged(s.toString())
                        onUpdate()
                    }
                })
            }
            )
            orientation = LinearLayout.VERTICAL
        }

        return view
    }

    fun interface TextChangeListener {
        fun onTextChanged(newText: String)
    }
}