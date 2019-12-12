package com.karacasoft.cmpe443carprojectcontroller

class KonamiCodeController {

    private var onKonamiCodeListener: (() -> Unit)? = null

    fun setOnKonamiCodeListener(onKonamiCodeListener: (() -> Unit)) {
        this.onKonamiCodeListener = onKonamiCodeListener
    }

    enum class InputType {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        B,
        A,
        START
    }

    var inputState = 0

    fun onInput(inputType: InputType) {
        if(inputState == 0 && inputType == InputType.UP) {
            inputState = 1
        } else if(inputState == 1 && inputType == InputType.UP) {
            inputState = 2
        } else if(inputState == 2 && inputType == InputType.DOWN) {
            inputState = 3
        } else if(inputState == 3 && inputType == InputType.DOWN) {
            inputState = 4
        } else if(inputState == 4 && inputType == InputType.LEFT) {
            inputState = 5
        } else if(inputState == 5 && inputType == InputType.RIGHT) {
            inputState = 6
        } else if(inputState == 6 && inputType == InputType.LEFT) {
            inputState = 7
        } else if(inputState == 7 && inputType == InputType.RIGHT) {
            inputState = 8
        } else if(inputState == 8 && inputType == InputType.B) {
            inputState = 9
        } else if(inputState == 9 && inputType == InputType.A) {
            inputState = 10
        } else if(inputState == 10 && inputType == InputType.START) {
            onKonamiCodeListener?.invoke()
            inputState = 0
        } else {
            inputState = 0
        }

    }

}