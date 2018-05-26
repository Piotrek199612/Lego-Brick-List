package com.example.piotrek.myapplication

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_inventory_parts.*
import kotlinx.android.synthetic.main.activity_start.*
import android.graphics.Bitmap
import android.media.Image


/**
 * Created by Piotrek on 17.05.2018.
 */
class InventoryPartsActivity : AppCompatActivity() {

    private var selectedItem:Int = -1
    private var Items = mutableListOf<MutableMap<String, String>>()
    private var Images = mutableMapOf<String, Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_parts)


        val bundle = intent.extras
        var id = bundle.getString("inventoryKey")
        setTitle(id)

        var db = DataBaseHelper(this)
        db.openDataBase()

        var cursor = db.readableDatabase.query("InventoriesParts" , arrayOf("ItemID, ColorID, QuantityInSet, QuantityInStore"), "InventoryID == ?", arrayOf(id), null, null, null)


        if (cursor.moveToFirst()) {
            var i = 0
            do {

                var tmp  = mutableMapOf<String,String> ()
                tmp["QuantityInStore"] = cursor.getString(cursor.getColumnIndex("QuantityInStore"))
                tmp["QuantityInSet"] = cursor.getString(cursor.getColumnIndex("QuantityInSet"))
                var ColorID = cursor.getString(cursor.getColumnIndex("ColorID"))
                var cursorColor = db.readableDatabase.query("Colors" , arrayOf("Name"), "_id == ?", arrayOf(ColorID), null, null, null)
                cursorColor.moveToFirst()
                tmp["Color"] = cursorColor.getString(cursorColor.getColumnIndex("Name"))
                cursorColor.close()
                var ItemID = cursor.getString(cursor.getColumnIndex("ItemID"))
                tmp["ItemID"] = ItemID
                var cursorItemID = db.readableDatabase.query("Parts" , arrayOf("Code, Name"), "_id == ?", arrayOf(ItemID), null, null, null)
                cursorItemID.moveToFirst()
                tmp["ItemName"] = cursorItemID.getString(cursorItemID.getColumnIndex("Name"))
                cursorItemID.close()

                tmp["ItemKey"] = i.toString()

                var cursorImage = db.readableDatabase.query("Codes" , arrayOf("Image"), "ItemID == ? and ColorID = ?", arrayOf(ItemID, ColorID), null, null, null)
                Log.i("PART","ITEMID $ItemID, COLORID $ColorID")
                if (cursorImage.count > 0) {
                    cursorImage.moveToFirst()
                    if (cursorImage.getBlob(cursorImage.getColumnIndex("Image")) != null)
                    {
                        var image = cursorImage.getBlob(cursorImage.getColumnIndex("Image"))
                        val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
                        Images[i.toString()] = bmp
                    }

                    //imageView.setImageBitmap(bmp)
                }

                i += 1

                Items.add(tmp)

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        fun selector(p: MutableMap<String, String>): Double? = (p["QuantityInStore"]!!.toDouble() / p["QuantityInSet"]!!.toDouble())
        Items.sortBy { selector(it) }
        DrawItems()
    }

    private fun DrawItems()
    {

        itemsLayout.removeAllViews()
        for (i in Items.indices) {
            var layout = LinearLayout(this)
            layout.orientation = LinearLayout.HORIZONTAL
            layout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            layout.setPadding(0, 50, 0, 50)
            layout.background = ContextCompat.getDrawable(this, R.drawable.border)


            var quantityLabel = TextView(this)
            quantityLabel.text = Items[i]["QuantityInStore"] + "/" + Items[i]["QuantityInSet"]
            quantityLabel.gravity = Gravity.CENTER
            quantityLabel.layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f)
            layout.addView(quantityLabel)


            var colorLabel = TextView(this)
            colorLabel.text = Items[i]["Color"]
            colorLabel.gravity = Gravity.CENTER
            colorLabel.layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
            layout.addView(colorLabel)


            var codeLabel = TextView(this)
            codeLabel.text = Items[i]["ItemName"]
            codeLabel.gravity = Gravity.CENTER
            codeLabel.layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
            layout.addView(codeLabel)
            if (i == selectedItem) {
                layout.setBackgroundColor(Color.GREEN)

                if (Images[Items[i]["ItemKey"]] != null) {
                    imageView.setImageBitmap(Images[Items[i]["ItemKey"]])
                }
                else
                {
                    imageView.setImageBitmap(null)
                }

            }
            else
                layout.setBackgroundColor(Color.WHITE)
            layout.setOnClickListener {
                selectedItem = i
                quantityChangeLabel.text = Items[i]["QuantityInStore"] + "/" + Items[i]["QuantityInSet"]
                Toast.makeText(this, "You clicked me.", Toast.LENGTH_SHORT).show()
                DrawItems()
            }

            itemsLayout.addView(layout)
        }
    }

    fun OnPlusClicked(v:View)
    {
        if (selectedItem != -1) {
            var inStore = Items[selectedItem]["QuantityInStore"]!!.toInt()
            var inSet = Items[selectedItem]["QuantityInSet"]!!.toInt()
            if (inStore < inSet) {
                Items[selectedItem]["QuantityInStore"] = (inStore + 1).toString()
                quantityChangeLabel.text = Items[selectedItem]["QuantityInStore"] + "/" + Items[selectedItem]["QuantityInSet"]
                DrawItems()
            }
        }
    }

    fun OnMinusClicked(v:View)
    {
        if (selectedItem != -1) {
            var inStore = Items[selectedItem]["QuantityInStore"]!!.toInt()
            if (inStore > 0) {
                Items[selectedItem]["QuantityInStore"] = (inStore - 1).toString()
                quantityChangeLabel.text = Items[selectedItem]["QuantityInStore"] + "/" + Items[selectedItem]["QuantityInSet"]
                DrawItems()
            }
        }
    }
}