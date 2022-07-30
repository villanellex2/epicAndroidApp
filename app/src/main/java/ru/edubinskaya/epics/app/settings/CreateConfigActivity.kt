package ru.edubinskaya.epics.app.settings

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.configuration.json.saveIfJsonCorrect


class CreateConfigActivity : AppCompatActivity() {
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
                saveIfJsonCorrect(name, findViewById<EditText>(R.id.config).text.toString(), db, this)
            }
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setMessage("Go back without saving?")
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ -> super.onBackPressed()}
            .show()
    }

    override fun onDestroy() {
        db?.close()
        super.onDestroy()
    }
}
