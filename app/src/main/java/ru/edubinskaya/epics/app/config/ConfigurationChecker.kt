package ru.edubinskaya.epics.app.config

import android.app.Activity
import org.json.JSONException
import org.json.JSONObject

fun checkConfigAndGetMessage(config: String, activity: Activity): String? {
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