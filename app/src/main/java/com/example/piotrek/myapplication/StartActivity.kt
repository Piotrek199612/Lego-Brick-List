package com.example.piotrek.myapplication

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import kotlinx.android.synthetic.main.activity_add_project.*
import java.net.HttpURLConnection
import java.net.URL
import android.os.StrictMode




class StartActivity : AppCompatActivity() {

    private val RED: Int = Color.rgb(211,50,50)
    private val GREEN: Int = Color.rgb(50,150,50)

    private var inventories: Cursor? = null
    private lateinit var db : DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        archivalButton.setBackgroundColor(RED)
        archivalButton.text = getString(R.string.archival_button_off)

        archiveButton.setBackgroundColor(Color.CYAN)
        addProjectButton.setBackgroundColor(Color.CYAN)

        db = DataBaseHelper(this)
        db.createDataBase()
        db.openDataBase()

        for (i :Int in 1..10)
        {
            val values = ContentValues()
            values.put("Name", "Project Name$i")
            values.put("Active", i%2)
            values.put("LastAccessed", i%4)
            db.writableDatabase.insert("Inventories", null, values)
        }
        db.close()
        getData()

    }

    override fun onRestart() {
        super.onRestart()
        getData()
    }

    fun getData()
    {
        db = DataBaseHelper(this)
        db.openDataBase()

        inventoryLayout.removeAllViews()

        var archive : String? = null
        if (archivalButton.text == getString(R.string.archival_button_off))
            archive = "Active <> 0"

        var cursor = db.readableDatabase.query("Inventories" , arrayOf("Name, Active, LastAccessed"), archive, null, null, null, "LastAccessed ASC")

        if (cursor.moveToFirst()) {
            do {
                var name = cursor.getString(cursor.getColumnIndex("Name"))
                    var layout = LinearLayout(this)
                    layout.orientation = LinearLayout.HORIZONTAL
                    layout.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)

                    var nameLabel = TextView(this)
                    nameLabel.text = name
                    nameLabel.gravity = Gravity.CENTER
                    nameLabel.layoutParams = LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);


                    var but1 = Button(this)
                    but1.text = "DETAILS"
                    but1.layoutParams = LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);

                    layout.addView(nameLabel)
                    layout.addView(but1)
                    inventoryLayout.addView(layout)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }
    fun archivalSwitch(view: View)
    {
        if (archivalButton.text == getString(R.string.archival_button_off))
        {
            archivalButton.text = getString(R.string.archival_button_on)
            archivalButton.setBackgroundColor(GREEN)
            getData()
        }
        else
        {
            archivalButton.text = getString(R.string.archival_button_off)
            archivalButton.setBackgroundColor(RED)
            getData()
        }
    }

    fun addProject(view:View)
    {
        Log.i("TEST","DZIALA")
        val intent = Intent(this, CreateProjectActivity::class.java)
        startActivity(intent)
    }
}
