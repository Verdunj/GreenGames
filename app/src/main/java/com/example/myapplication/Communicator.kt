package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Communicator : ViewModel() {
    val message = MutableLiveData<Any>()

    fun setMsgGameId(msg:Int){
        message.value = msg
    }

    fun setMsgCommunicator(msg:Int){
        message.value = msg
    }
}