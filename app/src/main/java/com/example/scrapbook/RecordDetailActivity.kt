package com.example.scrapbook

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_record_detail.*
import java.util.*

class RecordDetailActivity : AppCompatActivity() {

    private var actionBar: ActionBar? = null
    private var dbHelper: DatabaseHelper? = null
    private var recordId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)
        // setting up actionBar
        actionBar = supportActionBar
        actionBar?.title = resources.getString(R.string.card_details)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        val intent = intent
        recordId = intent.getStringExtra("RECORD_ID")

        showRecordDetails()
    }

    private fun showRecordDetails() {
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_ID} =\"$recordId\""

        val db = dbHelper?.writableDatabase
        val cursor = db?.rawQuery(selectQuery, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = "${cursor.getInt(cursor.getColumnIndex(Constants.C_ID))}"
                    val image = cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                    val title = cursor.getString(cursor.getColumnIndex(Constants.C_TITLE))
                    val category = cursor.getString(cursor.getColumnIndex(Constants.C_CATEGORY))
                    val location = cursor.getString(cursor.getColumnIndex(Constants.C_LOCATION))
                    val mood = cursor.getString(cursor.getColumnIndex(Constants.C_MOOD))
                    val description = cursor.getString(cursor.getColumnIndex(Constants.C_DESCRIPTION))
                    val addedTime = cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP))
                    val updatedTime = cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                    //convert timeStamp
                    val cal = Calendar.getInstance(Locale.getDefault())
                    cal.timeInMillis = addedTime.toLong()
                    val dateAndTimeAdded = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:ss", cal)

                    val calendar = Calendar.getInstance(Locale.getDefault())
                    calendar.timeInMillis = updatedTime.toLong()
                    val dateAndTimeUpdated = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:ss", calendar)

                    //set data
                    title_textView.text = title
                    category_textView.text = category
                    location_textView.text = location
                    mood_textView.text = mood
                    description_textView.text = description
                    addedDate_textView.text = dateAndTimeAdded
                    updatedDate_textView.text = dateAndTimeUpdated

                    if (image == "null") {
                        // no picture taken, set default
                        card_image.setImageResource(R.drawable.ic_image_black)
                    } else {
                        card_image.setImageURI(Uri.parse(image))
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            db.close()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}