package ru.edubinskaya.epics.app.screencreation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import ru.edubinskaya.epics.app.configuration.SizeInfo
import ru.edubinskaya.epics.app.configuration.SizeInfoType
import ru.edubinskaya.epics.app.configuration.containers.list.List
import ru.edubinskaya.epics.app.configuration.containers.list.ListInfo
import ru.edubinskaya.epics.app.configuration.containers.list.ListOrientation
import ru.edubinskaya.epics.app.configuration.json.createScreenFromJson
import ru.edubinskaya.epics.app.databinding.ActivitySreenCreationBinding


class CreateScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySreenCreationBinding
    lateinit var listInfo: ListInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySreenCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.add.setOnClickListener {
            startActivityForResult(Intent(this, CreateItemActivity::class.java), 1)
        }

        binding.done.setOnClickListener {
            if (binding.filename.text.isEmpty()) {
                MaterialAlertDialogBuilder(this).setTitle("Can't save file")
                    .setMessage("File name is empty")
                    .setPositiveButton("Change name") {_, _ -> }
                    .show()
                return@setOnClickListener
            }

            createScreenFromJson(binding.filename.text.toString(),  listInfo.toJson(), this)
        }

        listInfo = ListInfo(
            ListOrientation.VERTICAL, SizeInfo(SizeInfoType.MATCH_PARENT),
            SizeInfo(SizeInfoType.MATCH_PARENT)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val item = data?.getStringExtra("item") ?: return
            listInfo.addItem(JSONObject(item))
            binding.container.apply {
                removeAllViews()
                addView(List(listInfo.toJson(), this@CreateScreenActivity, null).view)
            }
        }
    }
}