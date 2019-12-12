package com.karacasoft.cmpe443carprojectcontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel : MainViewModel

    private lateinit var messageListRecyclerViewAdapter: MessageListRecyclerViewAdapter

    private var carManager: CarManager? = null

    private val konamiCodeListener = KonamiCodeController()
    private var konamiCodeActivateCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        CarManager.initService(this)

        messageListRecyclerViewAdapter = MessageListRecyclerViewAdapter(viewModel.getMessageList().value!!)

        viewModel.getMessageList().observe(this, Observer<List<MainViewModel.BTMessage>> {
            messageListRecyclerViewAdapter.notifyDataSetChanged()
            main_recycler_view_bluetooth_log.scrollToPosition(it.size - 1)
        })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        main_recycler_view_bluetooth_log.layoutManager = layoutManager

        main_recycler_view_bluetooth_log.adapter = messageListRecyclerViewAdapter


        main_btn_forward.setOnClickListener {
            carManager?.writeData("FORWARD")
            viewModel.onSendData("FORWARD")
            konamiCodeListener.onInput(KonamiCodeController.InputType.UP)

        }

        main_btn_backward.setOnClickListener {
            carManager?.writeData("BACK")
            viewModel.onSendData("BACK")
            konamiCodeListener.onInput(KonamiCodeController.InputType.DOWN)
        }

        main_btn_left.setOnClickListener {
            carManager?.writeData("LEFT")
            viewModel.onSendData("LEFT")
            konamiCodeListener.onInput(KonamiCodeController.InputType.LEFT)
        }

        main_btn_right.setOnClickListener {
            carManager?.writeData("RIGHT")
            viewModel.onSendData("RIGHT")
            konamiCodeListener.onInput(KonamiCodeController.InputType.RIGHT)
        }

        main_btn_stop.setOnClickListener {
            carManager?.writeData("STOP")
            viewModel.onSendData("STOP")
        }

        main_btn_auto_mode.setOnClickListener {
            carManager?.writeData("AUTO")
            viewModel.onSendData("AUTO")
            konamiCodeListener.onInput(KonamiCodeController.InputType.A)
        }

        main_btn_test_mode.setOnClickListener {
            carManager?.writeData("TEST")
            viewModel.onSendData("TEST")
            konamiCodeListener.onInput(KonamiCodeController.InputType.B)
        }

        main_btn_start.setOnClickListener {
            carManager?.writeData("START")
            viewModel.onSendData("START")
            konamiCodeListener.onInput(KonamiCodeController.InputType.START)
        }

        main_btn_status_request.setOnClickListener {
            carManager?.writeData("STATUS")
            viewModel.onSendData("STATUS")
        }

        konamiCodeListener.setOnKonamiCodeListener {
            konamiCodeActivateCount++
            if(konamiCodeActivateCount == 5) {
                AlertDialog.Builder(this)
                    .setTitle("Maybe..")
                    .setMessage("If you do this enough times, maybe your car can reach the speed of light?")
                    .show()
            } else if(konamiCodeActivateCount == 11){
                AlertDialog.Builder(this)
                    .setTitle("Why?")
                    .setMessage("Don't you have anything better to do?")
                    .show()
            } else if(konamiCodeActivateCount == 12){
                AlertDialog.Builder(this)
                    .setTitle("Cheat Code Activated")
                    .setMessage("Your car is now 0.001% faster, (maybe)")
                    .show()
            } else if(konamiCodeActivateCount == 13){
                AlertDialog.Builder(this)
                    .setTitle("Use the force")
                    .setMessage("Trust your instincts, I know you can do this")
                    .show()
            } else if(konamiCodeActivateCount == 14){
                AlertDialog.Builder(this)
                    .setTitle("Never gonna give you up")
                    .setMessage("Never gonna let you down...")
                    .show()
            } else if(konamiCodeActivateCount == 15){
                AlertDialog.Builder(this)
                    .setTitle("I know what you are up to")
                    .setMessage("Don't worry, I will not tell the others")
                    .show()
            } else if(konamiCodeActivateCount == 16){
                AlertDialog.Builder(this)
                    .setTitle("Machine Learning")
                    .setMessage("...is just if/else statements, prove me wrong")
                    .show()
            } else if(konamiCodeActivateCount == 17){
                AlertDialog.Builder(this)
                    .setTitle("Hmmm...")
                    .setMessage("Siz bir bakın bakalım buna...")
                    .show()
            } else if(konamiCodeActivateCount == 30){
                AlertDialog.Builder(this)
                    .setTitle("Cheat Code Activated")
                    .setMessage("You now officially have no life")
                    .show()
            } else if(konamiCodeActivateCount == 50) {
                AlertDialog.Builder(this)
                    .setTitle("wow")
                    .setMessage("This is getting interesting")
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Cheat Code Activated")
                    .setMessage("Your car is now 0.0001% faster, (probably)")
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.connect_to_device -> {
                CarManager.selectedDevice = null
                val intent = Intent(this, SelectDeviceActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if(CarManager.selectedDevice != null) {
            if(carManager != null) {
                carManager!!.disconnect()
            }
            carManager = CarManager(CarManager.selectedDevice!!)
            carManager!!.init()
            carManager!!.connect()
            carManager!!.setOnBluetoothDataRead { buffer, _ ->
                viewModel.onReadData(String(buffer?: ByteArray(0)))
                messageListRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeHandlerThread()
    }
}
