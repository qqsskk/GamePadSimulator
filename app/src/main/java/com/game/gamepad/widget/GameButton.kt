package com.game.gamepad.widget

import android.content.Context
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.game.gamepad.R
import com.game.gamepad.bluetooth.BlueToothTool

//import io.javac.ManyBlue.ManyBlue


class GameButton(
    context: Context,
    private val viewGroup: ViewGroup,
    listener: RemoveListener,
    key: String = "",
    x: Float,
    y: Float,
    private var radius: Int
) : View.OnClickListener, View.OnTouchListener {
    private val TAG = "GameButton"
    private var key = ""
    private var MOVESTATE = false//移动
    private lateinit var listener: RemoveListener
    //这里的attachtoroot是真的坑，直接添加到根视图下了
    private var layout: LinearLayout = LayoutInflater.from(context).inflate(
        R.layout.game_button_layout_ring,
        viewGroup,
        false
    ) as LinearLayout
    private var button: Button
    private var close: Button

    init {
        layout.x = x
        layout.y = y
        this.listener = listener
        viewGroup.addView(layout)
        button = layout.findViewById(R.id.btn)
        close = layout.findViewById(R.id.close)
        setText(key)
        //button.setOnClickListener(this)
        button.layoutParams.apply {
            height = radius*2
            width = radius*2
        }
        close.setOnClickListener(this)
        button.setOnTouchListener(this)
    }

    fun getLayout():LinearLayout{
        return layout
    }

    /**
     * 按钮进入可操作状态 移动、删除
     */
    fun setModifyState(state: Boolean) {
        MOVESTATE = state
        if (MOVESTATE) {
            if (close.visibility != View.VISIBLE)
                close.visibility = View.VISIBLE
        } else {
            if (close.visibility != View.INVISIBLE)
                close.visibility = View.INVISIBLE
        }
    }

    fun destroy(remove:Boolean){
        viewGroup.removeView(layout)
        if (remove)
            listener.remove(this)
    }

    fun setText(text: String) {
        key = text
        button.text = key
    }

    override fun onClick(v: View?) {
        if (v == null) return
        when (v.id) {
            R.id.close -> {
                destroy(true)//删除这个按钮
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null || v == null) return false
        if (MOVESTATE) {
            this.layout.x = event.rawX - button.width / 2 + close.width
            this.layout.y = event.rawY - button.height / 2
            return true
        }
        if (v.id == R.id.btn && event.action != MotionEvent.ACTION_MOVE) {
//            ManyBlue.blueWriteData("$key:$keyState",ManyBlue.getConnTagAll())
            //if (BlueTooth.connected) BlueTooth.send(key,keyState)
            //if (BlueToothTool.isConnected())
            //Log.e("SL","action:${event.action}")
            if (BlueToothTool.isConnected())
                BlueToothTool.sendMsg(
                    "$key:${when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            true
                        };MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            false
                        };else -> {
                            false
                        }
                    }}"
                )
        }
        return true
    }

    fun getBean():String{
        return """
            {"height":${button.height},"key":"$key","width":${button.width},"x":${layout.x},"y":${layout.y},"r":${radius}}
        """.trimIndent()
    }

    interface RemoveListener{
        fun remove(button:GameButton)
    }
}