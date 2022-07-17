package ru.edubinskaya.epics.app.configurationModel.containers

import androidx.appcompat.app.AlertDialog
import org.json.JSONException
import ru.edubinskaya.epics.app.configurationModel.ScreenUnit
import ru.edubinskaya.epics.app.configurationModel.fields.*
import ru.edubinskaya.epics.app.configurationModel.fields.mbbi.MbbiField
import ru.edubinskaya.epics.app.configurationModel.fields.text.InputTextField
import ru.edubinskaya.epics.app.configurationModel.fields.text.TextField

interface Container: ScreenUnit {
    val children: List<ScreenUnit>

    fun getSubViewList(): List<ScreenUnit> {
        val listOfFields = ArrayList<ScreenUnit>()
        try {
            val root = jsonRoot
            val jsonArray = jsonRoot.getJSONArray("content")

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                try {
                    val type = obj.getString("type")
                    val field = when (type) {
                        FieldType.TEXT_FIELD.name -> TextField(obj, activity, screenConfig)
                        FieldType.TEXT_INPUT_NUMBER.name -> InputTextField(obj, activity, screenConfig)
                        FieldType.GRAPH.name -> GraphField(obj, activity, screenConfig)
                        FieldType.BOOLEAN_INPUT.name -> BinaryField(true, obj, activity, screenConfig)
                        FieldType.BOOLEAN_FIELD.name -> BinaryField(false, obj, activity, screenConfig)
                        FieldType.MBBI.name -> MbbiField(obj, activity, screenConfig)
                        ContainerType.LIST.name -> ru.edubinskaya.epics.app.configurationModel.containers.list.List(
                            obj,
                            activity,
                            screenConfig
                        )
                        ContainerType.TABLE.name -> Table(obj, activity, screenConfig)
                        else -> null
                    }
                    if (field == null) showAlert("Incorrect field type: $type in $obj")
                    else listOfFields.add(field)
                } catch (e: JSONException) {
                    showAlert("${e.message} in$obj")
                }
            }
        } catch (ignored: JSONException) {
            val kotik = "dfdf"
        }
        return listOfFields
    }

    private fun showAlert(m: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setMessage(m)
                .setTitle("Incorrect json. Field skipped.")
                .setNegativeButton("OK") { _, _ -> }
                .show()
        }
    }

    override fun onDetachView() = children.forEach { child -> child.onDetachView() }

    override fun createMonitor() = children.forEach { child -> child.createMonitor() }

    override fun onChannelCreated()  = children.forEach { child -> child.onChannelCreated()}
}