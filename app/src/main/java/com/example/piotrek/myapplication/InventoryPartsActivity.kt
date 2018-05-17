package com.example.piotrek.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_inventory_parts.*
import kotlinx.android.synthetic.main.activity_start.*

/**
 * Created by Piotrek on 17.05.2018.
 */
class InventoryPartsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_parts)

        val bundle = intent.extras
        var id = bundle.getString("inventoryKey")
        testLabel.text = id

        var db = DataBaseHelper(this)
        db.openDataBase()

        var cursor = db.readableDatabase.query("InventoriesParts" , arrayOf("_id, ItemID"), "InventoryID == ?", arrayOf(id), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                var name = cursor.getString(cursor.getColumnIndex("_id"))
                Log.i("KEY", name)
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
                layout.addView(nameLabel)

                var ItemID = cursor.getString(cursor.getColumnIndex("ItemID"))
                var cursor1 = db.readableDatabase.query("Parts" , arrayOf("Code, Name"), "_id == ?", arrayOf(ItemID), null, null, null)
                cursor1.moveToFirst()
                var code = cursor1.getString(cursor1.getColumnIndex("Name"))
                var codeLabel = TextView(this)
                codeLabel.text = code
                codeLabel.gravity = Gravity.CENTER
                codeLabel.layoutParams = LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
                layout.addView(codeLabel)
                cursor1.close()

                itemsLayout.addView(layout)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }
}