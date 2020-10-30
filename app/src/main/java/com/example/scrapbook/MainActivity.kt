package com.example.scrapbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.example.scrapbook.databinding.ActivityMainBinding

const val NEWEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} DESC"

class MainActivity : AppCompatActivity() {

    // database helper
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        loadRecords()

        binding.recyclerView.setHasFixedSize(true)

        binding.setFabClicked {
            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("editMode", false)
            startActivity(intent)
        }
    }

    private fun loadRecords() {
        val cardAdapter = CardAdapter(this, dbHelper.getAllRecords(NEWEST_FIRST))

        binding.recyclerView.adapter = cardAdapter
    }

    private fun searchRecords(query: String) {
        val cardAdapter = CardAdapter(this, dbHelper.searchRecords(query))

        binding.recyclerView.adapter = cardAdapter
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchRecords(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchRecords(query)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}