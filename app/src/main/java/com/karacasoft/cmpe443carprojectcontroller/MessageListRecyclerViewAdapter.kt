package com.karacasoft.cmpe443carprojectcontroller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageListRecyclerViewAdapter(private val data: List<MainViewModel.BTMessage>) : RecyclerView.Adapter<MessageListRecyclerViewAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_CAR = 0
    private val VIEW_TYPE_CONTROLLER = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = if (viewType == VIEW_TYPE_CAR) inflater.inflate(R.layout.list_item_message_car, parent, false)
                   else inflater.inflate(R.layout.list_item_message_ctrl, parent, false)
        return MessageViewHolder(view, viewType)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if(data[position].source == MainViewModel.BTMessageSource.CAR) VIEW_TYPE_CAR else VIEW_TYPE_CONTROLLER
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.textView.text = data[position].message
    }

    class MessageViewHolder(itemView: View, val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.list_item_message_text)
    }
}