package com.karacasoft.cmpe443carprojectcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_device.*

class SelectDeviceActivity : AppCompatActivity() {
    private val TAG = "SelectDevActivity"

    private val btRequestCode = 0x100
    private val permissionRequestCode = 0x200

    private val devicesFound : MutableSet<BluetoothDevice> = HashSet()
    private val adapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val listAdapter = SelectDeviceListRecyclerViewAdapter(devicesFound)

    private val discoverReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    devicesFound.add(device)
                    listAdapter.notifyDataSetChanged()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    adapter.startDiscovery()
                }
            }
        }
    }


    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, btRequestCode)
    }

    private fun startDiscovery() {
        if(!adapter.isEnabled) {
            requestBluetoothEnable()
        } else {
            adapter.startDiscovery()
        }
    }

    private fun checkPermissions() : Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionRequestCode)
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == btRequestCode) {
            if(resultCode == RESULT_OK) {
                adapter.startDiscovery()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            permissionRequestCode -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_device)

        devicesFound.addAll(adapter.bondedDevices)

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(discoverReceiver, filter)

        if(checkPermissions()) {
            startDiscovery()
        }

        select_device_device_list.layoutManager = LinearLayoutManager(this)
        select_device_device_list.adapter = listAdapter
        listAdapter.setOnDeviceClickListener {
            CarManager.selectedDevice = it
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(discoverReceiver)
    }
}
