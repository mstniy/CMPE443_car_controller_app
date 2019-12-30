package com.karacasoft.cmpe443carprojectcontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.buttons_auto.view.*
import kotlinx.android.synthetic.main.buttons_manual.view.*
import kotlinx.android.synthetic.main.buttons_test.view.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel : MainViewModel

    private lateinit var messageListRecyclerViewAdapter: MessageListRecyclerViewAdapter

    private var carManager: CarManager? = null

    private val konamiCodeListener = KonamiCodeController()
    private var konamiCodeActivateCount = 0

    private var manualLastLeftDC = 0.0
    private var manualLastRightDC = 0.0

    private fun sendMessage(data : String) {
        carManager?.writeData(data)
        viewModel.onSendData(data)
    }

    fun switchToAuto() {
        buttons_frame.removeAllViews()
        var inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.buttons_auto, buttons_frame, true)

        buttons_frame.auto_btn_startstop.setOnClickListener {
            carManager?.writeData("START")
            viewModel.onSendData("START")
            konamiCodeListener.onInput(KonamiCodeController.InputType.START)
        }

        buttons_frame.auto_btn_status_request.setOnClickListener {
            carManager?.writeData("STATUS")
            viewModel.onSendData("STATUS")
        }
    }

    fun switchToTest() {
        buttons_frame.removeAllViews()
        var inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.buttons_test, buttons_frame, true)

        buttons_frame.test_btn_forward.setOnClickListener {
            carManager?.writeData("FORWARD")
            viewModel.onSendData("FORWARD")
            konamiCodeListener.onInput(KonamiCodeController.InputType.UP)

        }

        buttons_frame.test_btn_backward.setOnClickListener {
            carManager?.writeData("BACK")
            viewModel.onSendData("BACK")
            konamiCodeListener.onInput(KonamiCodeController.InputType.DOWN)
        }

        buttons_frame.test_btn_left.setOnClickListener {
            carManager?.writeData("LEFT")
            viewModel.onSendData("LEFT")
            konamiCodeListener.onInput(KonamiCodeController.InputType.LEFT)
        }

        buttons_frame.test_btn_right.setOnClickListener {
            carManager?.writeData("RIGHT")
            viewModel.onSendData("RIGHT")
            konamiCodeListener.onInput(KonamiCodeController.InputType.RIGHT)
        }

        buttons_frame.test_btn_stop.setOnClickListener {
            carManager?.writeData("STOP")
            viewModel.onSendData("STOP")
        }

        buttons_frame.test_btn_status_request.setOnClickListener {
            carManager?.writeData("STATUS")
            viewModel.onSendData("STATUS")
        }
    }

    private fun updateManualDC() {
        val currentLeftDC = buttons_frame.manual_seekbar_left.progress/100.0
        val currentRightDC = buttons_frame.manual_seekbar_right.progress/100.0

        if (currentLeftDC == manualLastLeftDC && currentRightDC == manualLastRightDC)
            return

        manualLastLeftDC = currentLeftDC
        manualLastRightDC = currentRightDC

        sendMessage(java.lang.String.format("DC %.02f %.02f", currentLeftDC, currentRightDC))
    }

    fun switchToManual() {
        buttons_frame.removeAllViews()
        var inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.buttons_manual, buttons_frame, true)
        manualLastLeftDC = 0.0
        manualLastRightDC = 0.0
        buttons_frame.manual_seekbar_left.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateManualDC()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        buttons_frame.manual_seekbar_right.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateManualDC()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        buttons_frame.manual_btn_status_request.setOnClickListener {
            carManager?.writeData("STATUS")
            viewModel.onSendData("STATUS")
        }
    }

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

        main_btn_auto_mode.setOnClickListener {
            switchToAuto()
            carManager?.writeData("AUTO")
            viewModel.onSendData("AUTO")
            konamiCodeListener.onInput(KonamiCodeController.InputType.A)
        }

        main_btn_test_mode.setOnClickListener {
            switchToTest()
            carManager?.writeData("TEST")
            viewModel.onSendData("TEST")
            konamiCodeListener.onInput(KonamiCodeController.InputType.B)
        }

        main_btn_manual_mode.setOnClickListener {
            switchToManual()
            carManager?.writeData("MANUAL")
            viewModel.onSendData("MANUAL")
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

        switchToAuto()
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
