package com.example.piotrek.myapplication

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    private val RED: Int = Color.rgb(211,50,50)
    private val GREEN: Int = Color.rgb(50,150,50)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        archivalButton.setBackgroundColor(RED)
        archivalButton.text = getString(R.string.archival_button_off)

        archiveButton.setBackgroundColor(Color.CYAN)
        addProjectButton.setBackgroundColor(Color.CYAN)
    }

    fun archivalSwitch(view: View)
    {
        if (archivalButton.text == getString(R.string.archival_button_off))
        {
            archivalButton.text = getString(R.string.archival_button_on)
            archivalButton.setBackgroundColor(GREEN)

        }
        else
        {
            archivalButton.text = getString(R.string.archival_button_off)
            archivalButton.setBackgroundColor(RED)
        }
    }
}
