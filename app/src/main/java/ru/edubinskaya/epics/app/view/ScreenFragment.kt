package ru.edubinskaya.epics.app.view

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.json.JSONException
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.channelaccess.EpicsContext
import ru.edubinskaya.epics.app.config.ScreenProvider
import ru.edubinskaya.epics.app.databinding.ScreenViewBinding
import ru.edubinskaya.epics.app.configurationModel.Screen

const val SERIALIZED_SCREEN_FIELD = "screen"
const val PV_NAME_DEVICE_FIELD = "pv_name"
const val DISPLAYED_NAME_DEVICE_FIELD = "displayed_name"

class SecondFragment : Fragment() {

    private var binding: ScreenViewBinding? = null

    private var screen: Screen? = null
    private var screenProvider: ScreenProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout? {
        binding = ScreenViewBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenProvider = ScreenProvider(activity)
        try {
            screen = arguments?.getSerializable(SERIALIZED_SCREEN_FIELD)
                ?.let { screenProvider!!.getScreenFields(it as Screen) }
            binding?.mainView?.addView(screen!!.view)
        } catch (e: JSONException) {
            activity?.let {
                AlertDialog.Builder(it)
                    .setMessage(e.message)
                    .setTitle("Incorrect json. Can't show screen.")
                    .setNegativeButton("OK") { _, _ ->
                        findNavController().navigate(
                            R.id.action_SecondFragment_to_FirstFragment,
                            Bundle()
                        ) }
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        InitialAsyncTask().execute()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        object : AsyncTask<Any?, Any?, Any?>() {
            override fun doInBackground(objects: Array<Any?>) {
                screen?.mainField?.onDetachView()
            }
        }.execute()
    }

    inner class InitialAsyncTask(): AsyncTask<Any, Any, Any>() {
        override fun doInBackground(vararg params: Any?): Any? {
            try { EpicsContext.context.pendIO(5.0) } catch (e: Exception) {}
            screen?.mainField?.createMonitor()
            try { EpicsContext.context.pendIO(5.0) } catch (e: Exception) {}
            return null
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            binding?.animationView?.visibility = View.GONE
        }
    }
}
