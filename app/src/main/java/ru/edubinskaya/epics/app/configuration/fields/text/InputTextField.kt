package ru.edubinskaya.epics.app.configuration.fields.text

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import gov.aps.jca.CAStatus
import gov.aps.jca.Channel
import gov.aps.jca.dbr.DBRType
import gov.aps.jca.event.GetEvent
import gov.aps.jca.event.GetListener
import gov.aps.jca.event.PutEvent
import gov.aps.jca.event.PutListener
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsContext


class InputTextField(
    jsonRoot: JSONObject,
    activity: Activity?,
    override val screenConfig: JSONObject?
) : TextField(jsonRoot, activity, screenConfig) {
    private val editText: EditText

    override fun blockInput() {
        editText.isClickable = false
    }

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_input_text, null) as LinearLayout
        prepareLayout()

        editText = view.findViewById(R.id.item_value)
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER)
        editText.inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
        editText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    editText.setTextColor(Color.GRAY)
                    SendNewValue().execute()
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        editText.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
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
            if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
                //TODO: очередь на Connected
                if (channel?.readAccess != true) {
                    Toast(activity).setText("No access")
                    return
                }

                when (channel?.fieldType) {
                    DBRType.DOUBLE -> editText.text.toString().toDoubleOrNull()?.let {
                        channel?.put(it, InputNumberPutListener())
                    }
                    DBRType.INT -> editText.text.toString().toIntOrNull()
                        ?.let { channel?.put(it, InputNumberPutListener()) }
                    DBRType.SHORT -> editText.text.toString().toDoubleOrNull()
                        ?.let { channel?.put(it, InputNumberPutListener()) }
                    DBRType.FLOAT -> editText.text.toString().toIntOrNull()
                        ?.let { channel?.put(it, InputNumberPutListener()) }
                    else -> {
                        return
                    }
                }
                EpicsContext.context.pendIO(3000.0)
            }
        }
    }

    inner class InputNumberPutListener() : PutListener {
        override fun putCompleted(ev: PutEvent?) {
            if (channel?.connectionState == Channel.ConnectionState.CONNECTED) {
                //todo: очередь на Connected
                channel?.get(InputNumberGetListener(ev))
                EpicsContext.context.pendIO(7000.0)
            }
        }
    }

    inner class InputNumberGetListener(val ev: PutEvent?) : GetListener {
        override fun getCompleted(event: GetEvent?) {
            if (event?.status == CAStatus.NORMAL) {
                editText.setText(event?.dbr?.asString())
                activity?.runOnUiThread {
                    if (ev?.status == CAStatus.NORMAL) {
                        ObjectAnimator.ofObject(
                            editText, "textColor", ArgbEvaluator(),
                            activity.resources?.getColor(R.color.text_color_success),
                            activity.resources?.getColor(R.color.text_color_primary),
                        ).setDuration(1000).start()
                    } else {
                        ObjectAnimator.ofObject(
                            editText, "textColor", ArgbEvaluator(),
                            Color.RED, activity.resources?.getColor(R.color.text_color_primary),
                        ).setDuration(1000).start()
                        Toast.makeText(activity, ev?.status?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
