package ru.edubinskaya.epics.app.configurationModel.fields

import MbbiRecyclerViewAdapter
import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val adapter: MbbiRecyclerViewAdapter

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_mbbi, null) as LinearLayout
        val recyclerView = view.findViewById<RecyclerView>(R.id.container)
        if (fieldLabel != null) {
            setDisplayName(jsonRoot)
            if (!hasDisplayName) {
                view.findViewById<TextView>(R.id.item_name).text = fieldLabel
            }
            initializeChannel()
        }
        val array = jsonRoot.getJSONArray("bits_to_show")
        val data = ArrayList<MbbiBitModel>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val bit = MbbiBitModel(
                obj.getInt("bit"),
                obj.getString("label"),
                i == 0 || i == 4
            )
            data.add(bit)
        }

        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = MbbiRecyclerViewAdapter(activity, data)
        recyclerView.adapter = adapter

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(15, 15, 15, 15)
        view.layoutParams = lp
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
