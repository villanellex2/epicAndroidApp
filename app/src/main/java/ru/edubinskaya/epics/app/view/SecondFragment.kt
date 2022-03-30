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
import ru.edubinskaya.epics.app.json.Screen

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

        screenProvider = ScreenProvider(activity)
        screen = arguments?.getString(TYPE_DEVICE_FIELD)
            ?.let { screenProvider!!.getScreenFieldsById(it.toInt()) }
        binding.mainView.addView(screen!!.view)

        val toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        toolbar?.title = screen?.displayedName + " (" + screen?.pvName + ")"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        object : AsyncTask<Any?, Any?, Any?>() {
            override fun doInBackground(objects: Array<Any?>) {
                screen?.mainField?.onDetachView()
            }
        }.execute()
    }
}
