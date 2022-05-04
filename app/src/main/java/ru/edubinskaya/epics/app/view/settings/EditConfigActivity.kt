package ru.edubinskaya.epics.app.view.settings

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.edubinskaya.epics.app.R
import java.io.*

internal val EDIT_FILE = "edit_file"

class EditConfigActivity : AppCompatActivity() {
//TODO: simple JSON validation
    val name: String get() = findViewById<TextView>(R.id.filename).text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_file_activity)

        findViewById<View>(R.id.done).setOnClickListener {
            saveFile()
        }

        val name = intent.extras?.getString(EDIT_FILE)
        if (name != null) {
            findViewById<TextView>(R.id.filename).text = name
            findViewById<EditText>(R.id.config).setText(readFile(name+".json"))
        } else {
            //TODO
        }
    }

    fun saveFile() {
        val name = this.name.replace(" ", "_")
        val config = findViewById<EditText>(R.id.config).text

        val fos = BufferedWriter(OutputStreamWriter(openFileOutput(name + ".json", MODE_PRIVATE)))
        fos.write(config.toString())
        fos.close()
        super.onBackPressed()
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