package org.wit.sportscouting.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.sportscouting.databinding.ActivitySportscoutingBinding
import org.wit.sportscouting.main.MainApp
import org.wit.sportscouting.models.SportScoutingModel
import timber.log.Timber.i
import org.wit.sportscouting.R
import android.view.View
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class SportScoutingActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySportscoutingBinding
    var sportscouting = SportScoutingModel()
    //val sportscoutings = ArrayList<SportScoutingModel>()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    lateinit var app: MainApp

    private var editIndex: Int = -1 //Variable to know if I'm editing a player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySportscoutingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        i("Sport Scouting Activity started...")

        registerImagePickerCallback()

        editIndex = intent.getIntExtra("editIndex", -1)
        if (editIndex != -1) {
            val item = app.sportscoutings[editIndex]

            // Keep a copy so we preserve ownerEmail
            sportscouting = item.copy()

            binding.player.setText(item.player)
            binding.team.setText(item.team)
            binding.position.setText(item.position)
            binding.btnAdd.text = getString(R.string.button_edit_player)

            // If there is an image -> display it
            if (item.image.isNotBlank()) {
                binding.imagePlayer.setImageURI(Uri.parse(item.image))
            }

            binding.btnDelete.visibility = View.VISIBLE
            binding.btnDelete.setOnClickListener { showDeleteOneDialog() }
        }

        binding.btnSelectImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnAdd.setOnClickListener() {
            sportscouting.player = binding.player.text.toString()
            sportscouting.team = binding.team.text.toString()
            sportscouting.position = binding.position.text.toString()

            if (sportscouting.player.isNotEmpty()) {
                if(editIndex == -1){ //Add player
                    // Assign current user as owner
                    val currentEmail = getCurrentUserEmail()
                    sportscouting.ownerEmail = currentEmail ?: ""

                    app.sportscoutings.add(sportscouting.copy())
                    i("add Button Pressed: $sportscouting")
                }
                else{ //Edit player
                    app.sportscoutings[editIndex] = sportscouting.copy()
                    i("edit Button Pressed (index=$editIndex): $sportscouting")
                }
                for (i in app.sportscoutings.indices) {
                    i("SportScouting[$i]:${this.app.sportscoutings[i]}")
                }

                app.persist()

                setResult(RESULT_OK)
                finish()
            }
            else {
                Snackbar
                    .make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    // File selector callback (ACTION_OPEN_DOCUMENT)
    private fun registerImagePickerCallback() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == RESULT_OK && result.data != null) {
                    val uri = result.data!!.data
                    if (uri != null) {

                        // Guardamos permiso para leer la imagen en futuros arranques
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        sportscouting.image = uri.toString()   // guardamos la URI
                        binding.imagePlayer.setImageURI(uri)   // la mostramos

                        i("Image selected: $uri")
                    }
                }
            }
    }

    // Open file selector
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
        }
        imagePickerLauncher.launch(intent)
    }

    private fun getCurrentUserEmail(): String? {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("logged_in", false)
        return if (loggedIn) {
            prefs.getString("user_email", null)
        } else {
            null   // without logging in -> “anonymous” player
        }
    }

    private fun showDeleteOneDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_one_title))
            .setNegativeButton(getString(R.string.action_cancel), null) // return to edition
            .setPositiveButton(getString(R.string.action_delete)) { _, _ ->
                if (editIndex in app.sportscoutings.indices) {
                    app.sportscoutings.removeAt(editIndex) // Delete player
                    app.persist()
                }
                setResult(RESULT_OK)
                finish() // return to the initial list
            }
            .show()
    }
}