package ru.edubinskaya.epics.app.configurationModel

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import org.json.JSONObject

interface ScreenUnit {
    val jsonRoot: JSONObject
    val screenConfig: JSONObject
    val view: View
    val activity: Activity?

    fun setViewLayoutParams() {
        val width = if (jsonRoot.has("width")) {
            val value = jsonRoot.getString("width")
            when (jsonRoot.getString("width")) {
                "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                else -> pxToDp(value.toIntOrNull())
            }
        } else ViewGroup.LayoutParams.MATCH_PARENT
        val height = if (jsonRoot.has("height")) {
            val value = jsonRoot.getString("height")
            when (jsonRoot.getString("height")) {
                "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
                "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
                else -> pxToDp(value.toIntOrNull())
            }
        } else ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams.width = width
        view.layoutParams.height = height
    }

    fun onDetachView()

    fun createMonitor()

    fun onChannelCreated()

    fun pxToDp(px: Int?): Int {
        if (px == null) return 0
        return if (activity != null) {
            val resources = activity?.resources
            val metrics = resources?.displayMetrics ?: return 0
            (px * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        } else {
            val metrics = Resources.getSystem().displayMetrics
            (px * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        }
    }
}
