package ru.edubinskaya.epics.app.configurationModel.fields.mbbi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.edubinskaya.epics.app.R


class MbbiRecyclerViewAdapter internal constructor(val context: Context?, private val data: List<MbbiBit>) :
    RecyclerView.Adapter<MbbiRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.mbbi_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.lable.setText(data[pos].label)
        holder.state.background = if (data[pos].state) context?.let {
            ContextCompat.getDrawable(it, R.drawable.mbbi_true)
        } else context?.let { ContextCompat.getDrawable(it, R.drawable.mbbi_false) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var lable: TextView = itemView.findViewById(R.id.mbbi_name)
        var state: View = itemView.findViewById(R.id.mbbi_state)

        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}