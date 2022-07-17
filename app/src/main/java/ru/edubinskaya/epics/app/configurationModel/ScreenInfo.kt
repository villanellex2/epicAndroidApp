package ru.edubinskaya.epics.app.configurationModel

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import ru.edubinskaya.epics.app.R
import java.io.Serializable


class ScreenInfo(
    val type: String?,
    val displayedName: String?,
    val root: String,
    val jsonObject: String
) : Serializable

class SizeInfo(
    private var type: SizeInfoType = SizeInfoType.WRAP_CONTENT,
    private var value: Int = 0
) {

    override fun toString(): String {
        return if (type == SizeInfoType.FIXED) {
            value.toString()
        } else {
            type.toString().lowercase()
        }
    }

    fun getParamSetter(
        title: String,
        activity: Activity?,
        value: SizeInfoType,
        onUpdate: () -> Unit
    ): View {
        this.type = value

        val view = activity?.layoutInflater?.inflate(R.layout.size_info_view, null) as LinearLayout
        val spinner = view.findViewById<Spinner>(R.id.spinner)

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            activity, R.array.size,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(value.ordinal)

        val editText = view.findViewById<EditText>(R.id.edit_text)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                this@SizeInfo.type = when (selectedItemPosition) {
                    0 -> {
                        editText.visibility = GONE
                        SizeInfoType.WRAP_CONTENT
                    }
                    1 -> {
                        editText.visibility = GONE
                        SizeInfoType.MATCH_PARENT
                    }
                    2 -> {
                        editText.visibility = VISIBLE
                        SizeInfoType.FIXED
                    }
                    else -> {
                        editText.visibility = GONE
                        SizeInfoType.WRAP_CONTENT
                    }
                }
                onUpdate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner.setSelection(this@SizeInfo.type.ordinal)
            }
        }

        view.findViewById<TextView>(R.id.title).text = title
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                this@SizeInfo.value = s.toString().toIntOrNull() ?: 50
                onUpdate()
            }
        })

        return view
    }
}

enum class SizeInfoType {
    WRAP_CONTENT,
    MATCH_PARENT,
    FIXED
}