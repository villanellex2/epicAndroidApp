package ru.edubinskaya.epics.app.view.settings

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import java.io.*

internal val EDIT_FILE = "edit_file"

class EditConfigActivity : AppCompatActivity() {
    val name: String get() = findViewById<TextView>(R.id.filename).text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_file_activity)

        val config = findViewById<EditText>(R.id.config)
        findViewById<View>(R.id.done).setOnClickListener {
            saveIfJsonCorrect(config.text.toString())
        }

        val name = intent.extras?.getString(EDIT_FILE)
        if (name != null) {
            findViewById<TextView>(R.id.filename).text = name
            findViewById<EditText>(R.id.config).setText(readFile(name+".json"))
        }
    }

    private fun saveIfJsonCorrect(config: String) {
        try {
            val json = JSONObject(config)
            val screens = json.getJSONArray("screens")
            val templates = json.getJSONObject("templates")

            if (screens.isNull(0)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Incorrect configuration")
                    .setMessage("\"screens\" can not be empty")
                    .setPositiveButton("EDIT CONFIGURATION") {_,_ -> }
                    .show()
            } else if (templates.length() == 0) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Incorrect configuration")
                    .setMessage("\"templates\" can not be empty")
                    .setPositiveButton("EDIT CONFIGURATION") {_,_ -> }
                    .show()
            } else {
                val fos =
                    BufferedWriter(OutputStreamWriter(openFileOutput(name + ".json", MODE_PRIVATE)))
                fos.write(config.toString())
                fos.close()
                super.onBackPressed()
            }
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

    fun readFile(filename: String): String {
        try {
            val br = BufferedReader(InputStreamReader(openFileInput(filename)))
            var line: String? = ""
            val sb = StringBuilder()

            while ((br.readLine().also { line = it }) != null) {
                sb.append(line)
                sb.append("\n")
            }
            return sb.toString()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}