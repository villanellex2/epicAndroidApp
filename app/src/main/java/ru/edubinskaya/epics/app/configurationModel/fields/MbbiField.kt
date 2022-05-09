package ru.edubinskaya.epics.app.configurationModel.fields

import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import org.json.JSONObject
import ru.edubinskaya.epics.app.R

private val fields = arrayOf(
    "ZRVL", "ONVL", "TWVL", "THVL", "FRVL",
    "FVVL", "SXVL", "SVVL", "EIVL", "NIVL", "TEVL",
    "ELVL", "TVVL", "TTVL", "FTVL", "FFVL"
)

class MbbiField(
    override val jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject
) : Field(jsonRoot, screenConfig) {
    override var view = LinearLayout(activity)
    override val monitorListener: MonitorListener = BinaryMonitorListener()
    override fun blockInput() {}

    override var fieldLabel: String? = pvName

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_mbbi, null) as LinearLayout

        if (fieldLabel != null) {
            setDisplayName(jsonRoot)
            if (!hasDisplayName) {
                view.findViewById<TextView>(R.id.item_name).text = fieldLabel
            }
            initializeChannel()
        }
        val array = jsonRoot.getJSONArray("bits_to_show")
    }

    inner class BinaryMonitorListener() : MonitorListener {

        override fun monitorChanged(event: MonitorEvent) {
            activity?.runOnUiThread {
                if (event.status.isSuccessful) {

                }
            }
        }
    }
}
