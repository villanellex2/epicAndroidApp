package ru.edubinskaya.epics.app.screencreation

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.configurationModel.SizeInfo
import ru.edubinskaya.epics.app.configurationModel.SizeInfoType
import ru.edubinskaya.epics.app.configurationModel.containers.list.List
import ru.edubinskaya.epics.app.configurationModel.containers.list.ListInfo
import ru.edubinskaya.epics.app.configurationModel.containers.list.ListOrientation
import ru.edubinskaya.epics.app.databinding.ActivitySreenCreationBinding
import ru.edubinskaya.epics.app.settings.saveIfJsonCorrect
import java.io.BufferedWriter
import java.io.OutputStreamWriter


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

            //TODO move to other class
            val json = JSONObject()
            json.put("screens", JSONArray().put(JSONObject().apply {
                put("type", "1")
                put("displayed_name", binding.filename.text.toString())
            }))
            json.put(
                "templates", JSONObject().put(
                    "1", listInfo.toJson()
                )
            )
            saveIfJsonCorrect(
                binding.filename.text.toString(),
                json.toString(),
                baseContext.openOrCreateDatabase("configuration.db", MODE_PRIVATE, null)
                    .apply { execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)") },
                this)
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