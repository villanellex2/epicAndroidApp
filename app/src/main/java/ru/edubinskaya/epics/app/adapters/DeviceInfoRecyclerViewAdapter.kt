import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.json.screen.Field


class DeviceInfoRecyclerViewAdapter internal constructor(context: Context?, data: List<Field>) :
    RecyclerView.Adapter<DeviceInfoRecyclerViewAdapter.ViewHolder>() {
    private val mData: List<Field> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.field, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.fieldName.text = mData[pos].fieldName
        if (holder.field.childCount > 1) {
            holder.field.removeViewAt(1)
        }
        holder.field.addView(mData[pos].view, 1)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var fieldName: TextView = itemView.findViewById<TextView>(R.id.item_name)
        var field = itemView.findViewById<GridLayout>(R.id.field)
        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    fun getItem(id: Int): Field {
        return mData[id]
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}