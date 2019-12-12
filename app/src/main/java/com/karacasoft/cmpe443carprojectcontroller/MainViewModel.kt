package com.karacasoft.cmpe443carprojectcontroller

import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val handler: Handler
    private val handlerThread: HandlerThread = HandlerThread("BT_THREAD")

    private val messageList: MutableList<BTMessage> = ArrayList()
    private val messageListLiveData: MutableLiveData<List<BTMessage>> by lazy {
        MutableLiveData<List<BTMessage>>().also {
            it.value = messageList
        }
    }

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun getMessageList(): LiveData<List<BTMessage>> {
        return messageListLiveData
    }

    fun onReadData(data: String) {
        messageList.add(BTMessage(BTMessageSource.CAR, data))
        messageListLiveData.value = messageList
    }

    fun onSendData(data: String) {
        messageList.add(BTMessage(BTMessageSource.CONTROLLER, data))
        messageListLiveData.value = messageList
    }

    fun closeHandlerThread() {
        handlerThread.interrupt()
    }

    enum class BTMessageSource {
        CAR,
        CONTROLLER
    }

    inner class BTMessage(val source: BTMessageSource, val message: String)


}