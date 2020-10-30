package com.example.scrapbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?): SQLiteOpenHelper(
    context, Constants.DB_NAME, null, Constants.DB_Version
) {
    override fun onCreate(p0: SQLiteDatabase) {
        // create database table
        p0.execSQL(Constants.CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        // if there is any structure change modify db version
        p0.execSQL("DROP TABLE IF EXISTS  " + Constants.TABLE_NAME)
        onCreate(p0)
    }

    fun insertCard(
        image: String?,
        title: String?,
        category: String?,
        location: String?,
        mood: String?,
        description: String?,
        addedTime: String?,
        updatedTime: String?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        // insert data
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TITLE, title)
        values.put(Constants.C_CATEGORY, category)
        values.put(Constants.C_LOCATION, location)
        values.put(Constants.C_MOOD, mood)
        values.put(Constants.C_DESCRIPTION, description)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)
        // insert row
        val id = db.insert(Constants.TABLE_NAME, null, values)
        db.close()
        // return id of inserted record
        return id
    }

    fun updateRecord(id: String,
                     image: String?,
                     title: String?,
                     category: String?,
                     location: String?,
                     mood: String?,
                     description: String?,
                     addedTime: String?,
                     updatedTime: String?) : Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TITLE, title)
        values.put(Constants.C_CATEGORY, category)
        values.put(Constants.C_LOCATION, location)
        values.put(Constants.C_MOOD, mood)
        values.put(Constants.C_DESCRIPTION, description)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)

        return db.update(Constants.TABLE_NAME,
            values,
            "${Constants.C_ID}=?",
            arrayOf(id)).toLong()
    }

    // get all data
    fun getAllRecords(orderBy: String) : ArrayList<CardModel> {

        val recordList = ArrayList<CardModel>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} ORDER BY '$orderBy'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        // looping through all records and adding them into the list
        if (cursor.moveToFirst()) {
            do {
                val cardRecord = CardModel(
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_TITLE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_CATEGORY)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_LOCATION)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_MOOD)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_DESCRIPTION)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP)),
                )
                // add record to list
                recordList.add(cardRecord)
            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()
        return recordList
    }
    // search data
    fun searchRecords(query: String) : ArrayList<CardModel> {
        val recordList = ArrayList<CardModel>()
        // query to select all records
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_TITLE} LIKE '%$query%'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val cardRecord = CardModel(
                    "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_TITLE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_CATEGORY)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_LOCATION)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_MOOD)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_DESCRIPTION)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP)),
                )
                // add record to list
                recordList.add(cardRecord)
            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()
        return recordList
    }
}