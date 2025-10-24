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

class SportScoutingActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySportscoutingBinding
    var sportscouting = SportScoutingModel()
    //val sportscoutings = ArrayList<SportScoutingModel>()
    lateinit var app: MainApp

    private var editIndex: Int = -1 //Variable to know if I'm editing a player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySportscoutingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        i("Sport Scouting Activity started...")

        editIndex = intent.getIntExtra("editIndex", -1)
        if (editIndex != -1) {
            val item = app.sportscoutings[editIndex]
            binding.sportscoutingTitle.setText(item.title)
            binding.description.setText(item.description)
            binding.position.setText(item.position)
            binding.btnAdd.text = getString(R.string.button_edit_player)

            binding.btnDelete.visibility = View.VISIBLE
            binding.btnDelete.setOnClickListener { showDeleteOneDialog() }
        }

        binding.btnAdd.setOnClickListener() {
            sportscouting.title = binding.sportscoutingTitle.text.toString()
            sportscouting.description = binding.description.text.toString()
            sportscouting.position = binding.position.text.toString()
            if (sportscouting.title.isNotEmpty()) {
                if(editIndex == -1){ //Add player
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