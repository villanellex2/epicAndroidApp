package ru.edubinskaya.epics.app.configurationModel.fields

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.configurationModel.ScreenItemsInfo
import ru.edubinskaya.epics.app.configurationModel.SizeInfo
import ru.edubinskaya.epics.app.configurationModel.SizeInfoType

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
            displayedName?.let { put("displayed_name", displayedName) }
        }
    }

    open fun getListOfParamSetters(activity: Activity): ArrayList<View> {
        val list = ArrayList<View>()

        list.add(createStringParamSetter("pv name", activity) { newText: String -> pvName = newText })
        list.add(createStringParamSetter("displayed name", activity) { newText: String -> displayedName = newText })
        list.add(height.getParamSetter("height", activity, SizeInfoType.WRAP_CONTENT))
        list.add(width.getParamSetter("width", activity, SizeInfoType.MATCH_PARENT))

        return list
    }

    fun createStringParamSetter(title: String, activity: Activity, onChangeListener: TextChangeListener): View {
        val view = activity.layoutInflater.inflate(R.layout.string_param_setter, null) as LinearLayout

        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<EditText>(R.id.edit_text).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                onChangeListener.onTextChanged(s.toString())
            }
        })

        return view
    }

    fun interface TextChangeListener {
        fun onTextChanged(newText: String)
    }
}