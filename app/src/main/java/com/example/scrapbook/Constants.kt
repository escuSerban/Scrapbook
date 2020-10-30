package com.example.scrapbook

object Constants {
    const val DB_Version = 1

    const val DB_NAME = "CARDS_DB"
    const val TABLE_NAME = "CARDS_TABLE"

    const val C_ID = "ID"
    const val C_IMAGE = "IMAGE"
    const val C_TITLE = "TITLE"
    const val C_CATEGORY = "CATEGORY"
    const val C_LOCATION = "LOCATION"
    const val C_MOOD = "MOOD"
    const val C_DESCRIPTION = "DESCRIPTION"
    const val C_ADDED_TIMESTAMP = "ADDED_TIME_STAMP"
    const val C_UPDATED_TIMESTAMP = "UPDATED_TIME_STAMP"

    const val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_IMAGE + " TEXT," + C_TITLE + " TEXT,"
            + C_CATEGORY + " TEXT," + C_LOCATION + " TEXT,"
            + C_MOOD + " TEXT," + C_DESCRIPTION + " TEXT,"
            + C_ADDED_TIMESTAMP + " TEXT,"
            + C_UPDATED_TIMESTAMP + " TEXT" + ")"
            )
}