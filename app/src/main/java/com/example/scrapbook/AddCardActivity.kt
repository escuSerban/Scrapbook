package com.example.scrapbook

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.scrapbook.databinding.ActivityAddCardBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_card.*

private const val CAMERA_REQUEST_CODE = 100
private const val STORAGE_REQUEST_CODE = 101
private const val IMAGE_SELECT_CAMERA_CODE = 102
private const val IMAGE_SELECT_GALLERY_CODE = 103

class AddCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCardBinding
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>

    private var imageUri: Uri? = null
    private var id: String? = ""
    private var title: String? = ""
    private var category: String? = ""
    private var location: String? = ""
    private var mood: String? = ""
    private var description: String? = ""
    private var addedTime: String? = ""
    private var updatedTime: String? = ""

    private var editMode = false

    private var actionBar: ActionBar? = null

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)

        actionBar = supportActionBar
        actionBar!!.title = getString(R.string.adding_entry)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        val intent = intent
        editMode = intent.getBooleanExtra("editMode", false)
        if (editMode) {
            actionBar?.title = resources.getString(R.string.update_option)

            id = intent.getStringExtra("ID")
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))
            title = intent.getStringExtra("TITLE")
            category = intent.getStringExtra("CATEGORY")
            location = intent.getStringExtra("LOCATION")
            mood = intent.getStringExtra("MOOD")
            description = intent.getStringExtra("DESCRIPTION")
            addedTime = intent.getStringExtra("ADDED_TIME")
            updatedTime = intent.getStringExtra("UPDATED_TIME")

            // if the user didn't attached photo while saving card then we set default image
            if (imageUri.toString() == "null") {
                section_imageView.setImageResource(R.drawable.ic_image_black)
            } else {
                section_imageView.setImageURI(imageUri)
            }
            binding.titleEditText.setText(title)
            binding.locationEditText.setText(location)
            binding.descriptionEditText.setText(description)

        } else {
            actionBar?.title = resources.getString(R.string.adding_entry)
        }

        dbHelper = DatabaseHelper(this)

        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        binding.setImageClicked { imagePickDialog() }

        binding.setSaveClicked { inputData() }

        ArrayAdapter.createFromResource(this, R.array.Categories, android.R.layout.simple_spinner_item).also {
                adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        // Handles spinner selection and saves it
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.categorySpinner.getItemAtPosition(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // When nothing is selected we make sure it gets the general name "Category"
                binding.categorySpinner.getItemAtPosition(0)
            }
        }

        // Spinner adapter required for drop-down functionality
        ArrayAdapter.createFromResource(this, R.array.Moods, android.R.layout.simple_spinner_item).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.moodSpinner.adapter = adapter
        }

        binding.moodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.moodSpinner.getItemAtPosition(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.moodSpinner.getItemAtPosition(0)
            }
        }
    }

    private fun inputData() {
        title = title_editText.text.toString().trim()
        category = category_spinner.selectedItem.toString().trim()
        location = location_editText.text.toString().trim()
        mood = mood_spinner.selectedItem.toString().trim()
        description = description_editText.text.toString().trim()

        if (editMode) {
            val timeStamp = "${System.currentTimeMillis()}"
            dbHelper.updateRecord(
                "$id",
                "$imageUri",
                "$title",
                "$category",
                "$location",
                "$mood",
                "$description",
                "$addedTime",
                "$timeStamp"
            )
            Toast.makeText(this, resources.getString(R.string.record_updated), Toast.LENGTH_SHORT).show()
        } else {
            val timestamp = System.currentTimeMillis()
            val id = dbHelper.insertCard(
                "" + imageUri, "" + title, "" + category,
                "" + location, "" + mood, "" + description,
                "" + "$timestamp", "" + "$timestamp"
            )
            Toast.makeText(this, "Card recorded with ID $id", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imagePickDialog() {

        val options = arrayOf("Camera", "Gallery")
        

        val builder = AlertDialog.Builder(this)

        builder.setTitle("pick image from")

        builder.setItems(options) { _, i ->
            if (i == 0) {
                if (!checkCameraPermissions()) requestCameraPermission() else selectFromCamera()
            } else {
                if (!checkStoragePermission()) requestStoragePermission() else selectFromGallery()
            }
        }
        builder.show()
    }

    private fun selectFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_SELECT_GALLERY_CODE)
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun selectFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Section Image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_SELECT_CAMERA_CODE)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        val initialResults = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val results = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        return initialResults && results
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        selectFromCamera()
                    } else {
                        Toast.makeText(
                            this,
                            "Camera and Storage permissions are required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        selectFromGallery()
                    } else {
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_SELECT_GALLERY_CODE) {
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            }
            else if (requestCode == IMAGE_SELECT_CAMERA_CODE) {
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    imageUri = resultUri
                    binding.sectionImageView.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
