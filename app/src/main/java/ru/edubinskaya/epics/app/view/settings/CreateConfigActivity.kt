package ru.edubinskaya.epics.app.view.settings

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import java.io.BufferedWriter
import java.io.OutputStreamWriter


class CreateConfigActivity : AppCompatActivity() {
//TODO: simple JSON validation
    val name: String get() = findViewById<EditText>(R.id.filename).text.toString()
    var db: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_file_activity)
        db = baseContext.openOrCreateDatabase("configuration.db", MODE_PRIVATE, null)
        db?.execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)")

        findViewById<View>(R.id.done).setOnClickListener {
            if (name.isEmpty()) {
                MaterialAlertDialogBuilder(this).setTitle("Can't save file")
                    .setMessage("File name is empty")
                    .setPositiveButton("Change name") {_, _ -> }
                    .show()
            } else {
                saveFile()
            }
        }
    }

    private fun saveFile() {
        val name = this.name.replace(" ", "_")
        if (isExist(name)) {
            MaterialAlertDialogBuilder(this).setTitle("Can't save file")
                .setMessage("File with name $name already exists")
                .setNegativeButton("Override file") { _, _ ->
                    val config = findViewById<EditText>(R.id.config).text

                    val fos = BufferedWriter(OutputStreamWriter(openFileOutput(name, MODE_PRIVATE)))
                    fos.write(config.toString())
                    super.onBackPressed()
                }
                .setPositiveButton("Change name") { _, _ -> }
                .show()
        } else {
            saveIfJsonCorrect(findViewById<EditText>(R.id.config).text.toString())
        }
    }

    private fun saveIfJsonCorrect(config: String) {
        try {
            val json = JSONObject(config)
            val screens = json.getJSONArray("screens")
            val templates = json.getJSONObject("templates")

            db?.execSQL("INSERT OR IGNORE INTO files VALUES (\"$name\")")
            val fos = BufferedWriter(OutputStreamWriter(openFileOutput(name + ".json", MODE_PRIVATE)))
            fos.write(config.toString())
            fos.close()

            super.onBackPressed()
        } catch (e: JSONException) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Incorrect configuration")
                .setMessage(e.message)
                .setPositiveButton("EDIT CONFIGURATION") {_,_ -> }
                .show()
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setMessage("Go back without saving?")
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ -> super.onBackPressed()}
            .show()
    }

    private fun isExist(name: String): Boolean {
        if (db == null) return true // TODO
        val query: Cursor = db!!.rawQuery("SELECT * FROM files WHERE (filename = \"$name\");", null)
        val res = query.count > 0
        query.close()
        return res
    }

    override fun onDestroy() {
        db?.close()
        super.onDestroy()
    }

}