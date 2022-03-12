package ru.edubinskaya.epics.app.view

import DeviceInfoRecyclerViewAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.BasicExample
import ru.edubinskaya.epics.app.config.DeviceListProvider
import ru.edubinskaya.epics.app.databinding.FragmentSecondBinding
import ru.edubinskaya.epics.app.model.Device
import ru.edubinskaya.epics.app.model.DeviceField
import ru.edubinskaya.epics.app.model.FieldType

const val TYPE_DEVICE_FIELD = "type"
const val PV_NAME_DEVICE_FIELD = "pv_name"
const val PV_VALUE_DEVICE_FIELD = "pv_value"
const val DISPLAYED_NAME_DEVICE_FIELD = "displayed_name"
const val PV_VALUE_CHANGED = "pv_value_changed"
const val PV_TYPE_DEVICE_TYPE = "pv_type"

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private var adapter: DeviceInfoRecyclerViewAdapter? = null
    private var deviceFields: List<DeviceField>? = null
    private var device: Device? = null
    private lateinit var example: BasicExample

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        val toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        toolbar?.title = device?.displayedName + " (" + device?.pvName + ")"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        device = Device(
            arguments?.getString(TYPE_DEVICE_FIELD),
            arguments?.getString(DISPLAYED_NAME_DEVICE_FIELD),
            arguments?.getString(PV_NAME_DEVICE_FIELD),
        )
        initializeRecyclerView()

        val toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        toolbar?.title = device?.displayedName + " (" + device?.pvName + ")"

        val filter = IntentFilter(PV_VALUE_CHANGED)
        context?.registerReceiver(PvChangedBroadcastReceiver(), filter)

        example = BasicExample(context)
        example.execute(deviceFields, device?.pvName)
    }

    private fun initializeRecyclerView() {
        binding.deviceFields.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        deviceFields = device?.type?.let { DeviceListProvider(context).getDeviceFieldsByType(it) }
        adapter = deviceFields?.let { DeviceInfoRecyclerViewAdapter(context, it) }
        binding.deviceFields.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        example.destroy()
    }

    private inner class PvChangedBroadcastReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (adapter != null) {
                val pvName = intent?.extras?.getString(PV_NAME_DEVICE_FIELD)
                val pvType = intent?.extras?.getInt(PV_TYPE_DEVICE_TYPE)?.let {
                    FieldType.values()[it]
                }
                when (pvType) {
                    FieldType.DOUBLE_VALUE -> adapter!!.setPvValue(
                        pvName,
                        intent.extras?.getDouble(PV_VALUE_DEVICE_FIELD)
                    )
                    FieldType.BOOLEAN_VALUE -> adapter!!.setPvValue(
                        pvName,
                        intent.extras?.getDouble(PV_VALUE_DEVICE_FIELD)
                    )
                }
            }
        }
    }
}