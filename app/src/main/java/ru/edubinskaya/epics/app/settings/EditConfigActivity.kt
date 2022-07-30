package ru.edubinskaya.epics.app.settings

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.configuration.json.checkConfigAndGetMessage
import java.io.*

internal val EDIT_FILE = "edit_file"

class EditConfigActivity : AppCompatActivity() {
    val name: String get() = findViewById<TextView>(R.id.filename).text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_file)

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
        val message = checkConfigAndGetMessage(config)
        if (message != null) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Incorrect configuration")
                .setMessage(message)
                .setPositiveButton("EDIT CONFIGURATION") { _, _ -> }
                .show()
        } else {
            val fos =
                BufferedWriter(OutputStreamWriter(openFileOutput(name + ".json", MODE_PRIVATE)))
            fos.write(config)
            fos.close()
            super.onBackPressed()
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