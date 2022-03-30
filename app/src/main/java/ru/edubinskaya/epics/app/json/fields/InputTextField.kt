package ru.edubinskaya.epics.app.json.fields

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.AsyncTask
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import gov.aps.jca.dbr.*
import org.json.JSONObject
import ru.edubinskaya.epics.app.R


class InputTextField(
    jsonRoot: JSONObject,
    prefix: String,
    activity: Activity?
) : TextField(jsonRoot, prefix, activity) {
    private val editText: EditText

    init {
        view = activity?.layoutInflater?.inflate(R.layout.input_text_field, null) as GridLayout
        prepareLayout()

        editText = view.findViewById(R.id.item_value)
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER)
        editText.inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
        editText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    editText.clearFocus()
                    editText.setTextColor(Color.GRAY)
                    SendNewValue().execute()
                    return true
                }
                return false
            }
        })

        editText.doOnTextChanged { text, start, before, count ->
            Log.d("changed", text.toString())
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class SendNewValue : AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(objects: Array<Any?>) {
            if (channel?.readAccess != true) {
                Toast(activity).setText("No access")
                return
            }


            when (channel?.fieldType){
                DBRType.DOUBLE -> editText.text.toString().toDoubleOrNull()?.let { channel?.put(it) }
                DBRType.INT -> editText.text.toString().toIntOrNull()?.let { channel?.put(it) }
                DBRType.SHORT -> editText.text.toString().toDoubleOrNull()?.let { channel?.put(it) }
                DBRType.FLOAT -> editText.text.toString().toIntOrNull()?.let { channel?.put(it) }
                else -> "Incorrect PV type for text field" //TODO
            }
        }
    }
}
