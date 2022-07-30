package ru.edubinskaya.epics.app.configuration.fields.mbbi

import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gov.aps.jca.event.MonitorEvent
import gov.aps.jca.event.MonitorListener
import org.json.JSONObject
import ru.edubinskaya.epics.app.R
import gov.aps.jca.dbr.ENUM
import ru.edubinskaya.epics.app.configuration.fields.Field

class MbbiField(
    override var jsonRoot: JSONObject,
    override val activity: Activity?,
    override val screenConfig: JSONObject?
) : Field(jsonRoot, screenConfig) {
    override var view = LinearLayout(activity)
    override val monitorListener: MonitorListener = MbbiMonitorListener()
    override fun blockInput() {}

    override lateinit var fieldLabel: String
    private val adapter: MbbiRecyclerViewAdapter
    private val data = ArrayList<MbbiBit>()

    init {
        view = activity?.layoutInflater?.inflate(R.layout.field_mbbi, null) as LinearLayout
        val recyclerView = view.findViewById<RecyclerView>(R.id.container)

        setDisplayName()
        initializeChannel()

        val array = jsonRoot.getJSONArray("bits_to_show")
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val bit = MbbiBit(
                obj.getInt("bit"),
                obj.getString("label"),
                false
            )
            data.add(bit)
        }

        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = MbbiRecyclerViewAdapter(activity, data)
        recyclerView.adapter = adapter

        prepareLayout()
    }

    inner class MbbiMonitorListener() : MonitorListener {
        override fun monitorChanged(event: MonitorEvent) {
            if (event.status.isSuccessful) {
                if (event.dbr?.isENUM == true) {
                    val short = (event.dbr as ENUM).enumValue[0]
                    for (bit in data) {
                        bit.state = bit.bit.toShort() == short
                    }
                }
                activity?.runOnUiThread { adapter.notifyDataSetChanged() }
            }
        }
    }
}
