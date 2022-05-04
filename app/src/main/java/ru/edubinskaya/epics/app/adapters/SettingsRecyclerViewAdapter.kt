import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.view.settings.CreateConfigActivity
import ru.edubinskaya.epics.app.view.settings.EDIT_FILE
import ru.edubinskaya.epics.app.view.settings.EditConfigActivity


class SettingsRecyclerViewAdapter internal constructor(val context: Context?, private val data: ArrayList<String>) :
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.filename_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.deviceName.text = data[pos]
        holder.pos = pos
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deviceName: TextView = itemView.findViewById(R.id.filename)
        var delete: TextView = itemView.findViewById(R.id.delete)
        var edit: TextView = itemView.findViewById(R.id.edit)
        var pos: Int? = null

        init {
            delete.setOnClickListener {
                val db = context?.openOrCreateDatabase("configuration.db", MODE_PRIVATE, null)
                db?.execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)")
                val name = deviceName.text.toString()
                db?.execSQL("DELETE FROM files WHERE (filename = \"$name\")")

                pos?.let {
                    this@SettingsRecyclerViewAdapter.data.removeAt(it)
                    this@SettingsRecyclerViewAdapter.notifyItemRemoved(pos!!)
                }

                db?.close()

                context?.deleteFile(name + ".json")
            }

            edit.setOnClickListener {
                val intent = Intent(context, EditConfigActivity::class.java)
                intent.putExtra(EDIT_FILE, deviceName.text.toString())
                context?.startActivity(intent)
            }
        }
    }
}