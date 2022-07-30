package ru.edubinskaya.epics.app.screencreation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import ru.edubinskaya.epics.app.configuration.SizeInfoType
import ru.edubinskaya.epics.app.configuration.fields.FieldInfo
import ru.edubinskaya.epics.app.configuration.fields.binaryfield.BinaryField
import ru.edubinskaya.epics.app.configuration.fields.binaryfield.BinaryInfo
import ru.edubinskaya.epics.app.configuration.fields.text.InputTextField
import ru.edubinskaya.epics.app.configuration.fields.text.TextField
import ru.edubinskaya.epics.app.configuration.fields.text.TextFieldInfo
import ru.edubinskaya.epics.app.databinding.ActivityCreateItemBinding


class CreateItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateItemBinding
    private var fieldInfo: FieldInfo? = null
    private var showView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                onFieldTypeChanged(selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.spinner.setSelection(0)
            }
        }
        binding.add.setOnClickListener {
            val intent = Intent()
            intent.putExtra("item", fieldInfo?.toJson().toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun onFieldTypeChanged(selectedItemPosition: Int) {
        val showView = when (selectedItemPosition) {
            1 -> {
                fieldInfo = TextFieldInfo()
                InputTextField((fieldInfo as TextFieldInfo).toJson(), this, null)
            }
            2 -> {
                fieldInfo = BinaryInfo()
                BinaryField(false, (fieldInfo as BinaryInfo).toJson(), this, null)
            }
            else -> {
                fieldInfo = TextFieldInfo()
                TextField((fieldInfo as TextFieldInfo).toJson(), this, null)
            }
        }
        binding.propertiesContainer.removeAllViews()
        binding.linearLayout.removeAllViews()

        fieldInfo?.getListOfParamSetters(this) {
            fieldInfo?.let { showView.jsonRoot = it.toJson() }
            showView.prepareLayout()
        }?.forEach { binding.propertiesContainer.addView(it) }
        binding.linearLayout.addView(showView.view)
    }
}