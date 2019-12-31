package com.karacasoft.cmpe443carprojectcontroller

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectDeviceListRecyclerViewAdapter(private val data : List<BluetoothDevice>):
    RecyclerView.Adapter<SelectDeviceListRecyclerViewAdapter.ViewHolder>() {

    private var onDeviceClickListener : ((device: BluetoothDevice) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = data.elementAt(position)
        holder.deviceNameTextView.text = device.name
        holder.deviceMacAddrTextView.text = device.address
        holder.view.setOnClickListener {
            onDeviceClickListener?.invoke(device)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val view = itemView
        val deviceNameTextView : TextView = itemView.findViewById(R.id.device_name)
        val deviceMacAddrTextView : TextView = itemView.findViewById(R.id.device_mac)

    }

    fun setOnDeviceClickListener(onDeviceClickListener: (device: BluetoothDevice) -> Unit) {
        this.onDeviceClickListener = onDeviceClickListener
    }
}