package ru.edubinskaya.epics.app.configuration.json

import android.app.Activity
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import java.io.BufferedWriter
import java.io.OutputStreamWriter

fun saveIfJsonCorrect(name: String, config: String, db: SQLiteDatabase?, activity: Activity) {
    if (isExist(name, db)) {
        MaterialAlertDialogBuilder(activity).setTitle("Can't save file")
            .setMessage("File with name $name already exists")
            .setNegativeButton("Override file") { _, _ ->
                val config = activity.findViewById<EditText>(R.id.config).text

                val fos = BufferedWriter(OutputStreamWriter(activity.openFileOutput(name,
                    AppCompatActivity.MODE_PRIVATE
                )))
                fos.write(config.toString())
                activity.onBackPressed()
            }
            .setPositiveButton("Change name") { _, _ -> }
            .show()
        return
    }
    val message = checkConfigAndGetMessage(config)
    if (message != null) {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Incorrect configuration")
            .setMessage(message)
            .setPositiveButton("EDIT CONFIGURATION") {_,_ -> }
            .show()
    } else {
        db?.execSQL("INSERT OR IGNORE INTO files VALUES (\"$name\")")
        val fos =
            BufferedWriter(
                OutputStreamWriter(activity.openFileOutput(
                "$name.json",
                AppCompatActivity.MODE_PRIVATE
            )))
        fos.write(config)
        fos.close()
        activity.onBackPressed()
    }
}

fun checkConfigAndGetMessage(config: String): String? {
    try {
        val json = JSONObject(config)
        val screens = json.getJSONArray("screens")
        val templates = json.getJSONObject("templates")

        if (screens.isNull(0)) {
            return "\"screens\" can not be empty"
        } else if (templates.length() == 0) {
            return "\"templates\" can not be empty"
        }
    } catch (e: JSONException) {
        val message = e.message ?: return null
        var regex = Regex("at character [0-9][0-9]*")
        val str = regex.find(message)
        val error = regex.find(message)?.range?.first?.let { message.substring(0, it) } ?: return message

        regex = Regex("[1-9][0-9]*")
        val num = str?.value?.let { regex.find(it)?.value?.toInt() } ?: return message

        regex = Regex("\n")
        var strings = regex.find(message)
        var line = 0

        while (strings != null) {
            if (strings.range.first < num) {
                line++
                strings = strings.next()
            } else {
                break
            }
        }
        line++
        val sb = StringBuilder()
        sb.append(error)
        sb.append("at line ")
        sb.append(line)
        sb.append(':')
        sb.append('\n')
        for (i in (line - 2)..(line + 2)) {
            if (i >= 0 || i < config.length) {
                sb.append('\n')
                sb.append(i)
                sb.append('\t')
                sb.append(message.lines()[i])
            }
        }
        return sb.toString()
    }
    return null
}

fun createScreenFromJson(screenName: String, template: JSONObject, activity: Activity) {
    val json = JSONObject()
    json.put("screens", JSONArray().put(JSONObject().apply {
        put("type", "1")
        put("displayed_name", screenName)
    }))
    json.put(
        "templates", JSONObject().put(
            "1", template
        )
    )
    saveIfJsonCorrect(
        screenName,
        json.toString(),
        activity.openOrCreateDatabase("configuration.db", AppCompatActivity.MODE_PRIVATE, null)
            .apply { execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)") },
        activity)
}

private fun isExist(name: String, db: SQLiteDatabase?): Boolean {
    if (db == null) return true
    val query: Cursor = db!!.rawQuery("SELECT * FROM files WHERE (filename = \"$name\");", null)
    val res = query.count > 0
    query.close()
    return res
}