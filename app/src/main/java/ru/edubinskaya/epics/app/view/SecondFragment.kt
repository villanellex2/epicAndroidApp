package ru.edubinskaya.epics.app.view

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.config.ScreenProvider
import ru.edubinskaya.epics.app.databinding.ScreenViewBinding
import ru.edubinskaya.epics.app.json.screen.Screen

const val TYPE_DEVICE_FIELD = "type"
const val PV_NAME_DEVICE_FIELD = "pv_name"
const val DISPLAYED_NAME_DEVICE_FIELD = "displayed_name"

class SecondFragment : Fragment() {

    private var _binding: ScreenViewBinding? = null

    private val binding get() = _binding!!

    private var screen: Screen? = null
    private var screenProvider: ScreenProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScreenViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screen = requireArguments().getString(PV_NAME_DEVICE_FIELD)?.let {
            Screen(
                arguments?.getString(TYPE_DEVICE_FIELD),
                arguments?.getString(DISPLAYED_NAME_DEVICE_FIELD),
                it, LinearLayout(context), emptyList()
            )
        }

        initializeContent()
        val toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        toolbar?.title = screen?.displayedName + " (" + screen?.pvName + ")"
    }

    private fun initializeContent() {
        screenProvider = ScreenProvider(activity)
        val screen = screen?.id?.let { screenProvider!!.getScreenFieldsById(it) } ?: return
        binding.mainView.addView(screen.view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}
