package ru.edubinskaya.epics.app.view

import ListOfDevicesRecyclerViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.edubinskaya.epics.app.config.ScreenProvider
import ru.edubinskaya.epics.app.databinding.FragmentFirstBinding
import ru.edubinskaya.epics.app.json.screen.Screen
import ru.edubinskaya.epics.app.R


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var listOfScreen: List<Screen>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()

        val toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        toolbar?.setTitle("List of devices.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeRecyclerView() {
        binding.listOfDevices.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listOfScreen = ScreenProvider(activity).screenList

        val adapter = ListOfDevicesRecyclerViewAdapter(context, listOfScreen)
        adapter.setClickListener(
            object : ListOfDevicesRecyclerViewAdapter.ItemClickListener{
                override fun onItemClick(view: View?, position: Int) {
                    val device = adapter.getItem(position)
                    val bundle = Bundle()
                    bundle.putString(PV_NAME_DEVICE_FIELD, device.pvName)
                    bundle.putString(DISPLAYED_NAME_DEVICE_FIELD, device.displayedName)
                    bundle.putString(TYPE_DEVICE_FIELD, device.id)
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
                }
            }
        )
        binding.listOfDevices.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        binding.listOfDevices.adapter = adapter
    }
}