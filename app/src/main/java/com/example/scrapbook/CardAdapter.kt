package com.example.scrapbook

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*
* Adapter class for recyclerView.
**/
class CardAdapter(private var context: Context?, private var recordList: ArrayList<CardModel>?) :
    RecyclerView.Adapter<CardAdapter.HolderRecord>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        // inflate the layout row_card.xml
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.row_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        // get data
        val model = recordList!![position]
        val id = model.id
        val image = model.image
        val title = model.title
        val category = model.category
        val location = model.location
        val mood = model.mood
        val description = model.description
        val addedTime = model.addedTime
        val updatedTime = model.updatedTime
        // set data
        holder.cardTitle.text = title
        holder.cardCategory.text = category
        holder.cardLocation.text = location
        holder.cardMood.text = mood
        holder.cardDescription.text = description

        if (image == "null") {
            // no picture taken, set default
            holder.cardImage.setImageResource(R.drawable.ic_image_black)
        } else {
            holder.cardImage.setImageURI(Uri.parse(image))
        }
        // showing the selected record when it's clicked
        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecordDetailActivity::class.java)
            intent.putExtra("RECORD_ID", id)
            context?.startActivity(intent)
        }
        // handling cardOptions click
        holder.cardOptions.setOnClickListener {
            showOptions(
                position,
                id,
                image,
                title,
                category,
                location,
                mood,
                description,
                addedTime,
                updatedTime
            )
        }
    }

    private fun showOptions(
        position: Int,
        id: String,
        image: String,
        title: String,
        category: String,
        location: String,
        mood: String,
        description: String,
        addedTime: String,
        updatedTime: String
    ) {
        val options = arrayOf("Edit", "Delete")
        val dialog: AlertDialog.Builder = AlertDialog.Builder(context)
        dialog.setItems(options) { dialog, i ->
            if (i == 0) {
                val intent = Intent(context, AddCardActivity::class.java)
                intent.putExtra("ID", id)
                intent.putExtra("IMAGE", image)
                intent.putExtra("TITLE", title)
                intent.putExtra("CATEGORY", category)
                intent.putExtra("LOCATION", location)
                intent.putExtra("MOOD", mood)
                intent.putExtra("DESCRIPTION", description)
                intent.putExtra("ADDED_TIME", addedTime)
                intent.putExtra("UPDATED_TIME", updatedTime)
                intent.putExtra("editMode", true)
                context?.startActivity(intent)
            } else {

            }
        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        // return records
       return recordList!!.size
    }

    class HolderRecord(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // views from row_card.xml
        var cardImage: ImageView = itemView.findViewById(R.id.card_imageView)
        var cardTitle: TextView = itemView.findViewById(R.id.title_section)
        var cardCategory: TextView = itemView.findViewById(R.id.category_section)
        var cardLocation: TextView = itemView.findViewById(R.id.location_section)
        var cardMood: TextView = itemView.findViewById(R.id.mood_section)
        var cardDescription: TextView = itemView.findViewById(R.id.description_section)
        var cardOptions: ImageButton = itemView.findViewById(R.id.more_button)
    }
}