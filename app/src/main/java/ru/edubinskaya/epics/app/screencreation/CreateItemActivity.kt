package ru.edubinskaya.epics.app.screencreation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.edubinskaya.epics.app.configurationModel.fields.text.TextField
import ru.edubinskaya.epics.app.configurationModel.fields.text.TextFieldInfo
import ru.edubinskaya.epics.app.databinding.ActivityCreateItemBinding


class CreateItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fieldInfo = TextFieldInfo()
        val showView = TextField(fieldInfo.toJson(), this, null)
        fieldInfo.getListOfParamSetters(this) {
            showView.jsonRoot = fieldInfo.toJson()
            showView.prepareLayout()
        }
            .forEach { binding.propertiesContainer.addView(it) }
        binding.linearLayout.addView(showView.view)
        binding.add.setOnClickListener {
            val intent = Intent()
            intent.putExtra("item", fieldInfo.toJson().toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}