import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.edubinskaya.epics.app.R
import ru.edubinskaya.epics.app.model.DeviceField
import ru.edubinskaya.epics.app.model.FieldType


class DeviceInfoRecyclerViewAdapter internal constructor(context: Context?, data: List<DeviceField>) :
    RecyclerView.Adapter<DeviceInfoRecyclerViewAdapter.ViewHolder>() {
    private val mData: List<DeviceField> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.device_double_value_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.fieldName.text = mData[pos].fieldName
        if (mData[pos].fieldType == FieldType.DOUBLE_VALUE) {
            holder.doubleValue.text = (mData[pos].fieldValue).toString()
            holder.doubleValue.visibility = View.VISIBLE
            holder.switch.visibility = View.GONE
        } else {
            if (mData[pos].fieldValue != null) {
                holder.switch.isChecked = (mData[pos].fieldValue) as Double > 0
            } else {
                holder.switch.isChecked = false
            }
            holder.doubleValue.visibility = View.GONE
            holder.switch.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var fieldName: TextView = itemView.findViewById<TextView>(R.id.item_name)
        var doubleValue: TextView= itemView.findViewById<TextView>(R.id.item_value)
        var switch = itemView.findViewById<Switch>(R.id.item_switch)

        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    fun getItem(id: Int): DeviceField {
        return mData[id]
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPvValue(pvName: String?, pvValue: Double?){
        if (pvName == null || pvValue == null) return
        for (field: DeviceField in mData){
            if (field.fieldName.equals(pvName)) {
                field.fieldValue = pvValue
            }
        }
        this.notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}