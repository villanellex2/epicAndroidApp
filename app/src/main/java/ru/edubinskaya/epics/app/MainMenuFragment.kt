package ru.edubinskaya.epics.app

import ru.edubinskaya.epics.app.configuration.ListOfDevicesRecyclerViewAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.edubinskaya.epics.app.configuration.json.ScreenProvider
import ru.edubinskaya.epics.app.configuration.ScreenInfo
import ru.edubinskaya.epics.app.databinding.FragmentMainMenuBinding
import ru.edubinskaya.epics.app.screencreation.CreateScreenActivity
import ru.edubinskaya.epics.app.settings.SettingsActivity


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var listOfScreen: List<ScreenInfo>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            startActivity(Intent(activity, CreateScreenActivity::class.java))
        }
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionSettings.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        initializeRecyclerView()
        super.onStart()
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
                    val bundle = Bundle()
                    bundle.putSerializable(SERIALIZED_SCREEN_FIELD, adapter.getItem(position))
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
                }
            }
        )
        binding.listOfDevices.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        binding.listOfDevices.adapter = adapter

        binding.empty.visibility = if (adapter.itemCount == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}