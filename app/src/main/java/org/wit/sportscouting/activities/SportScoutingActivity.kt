package org.wit.sportscouting.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.sportscouting.databinding.ActivitySportscoutingBinding
import org.wit.sportscouting.main.MainApp
import org.wit.sportscouting.models.SportScoutingModel
import timber.log.Timber.i

class SportScoutingActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySportscoutingBinding
    var sportscouting = SportScoutingModel()
    //val sportscoutings = ArrayList<SportScoutingModel>()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySportscoutingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        i("Sport Scouting Activity started...")

        binding.btnAdd.setOnClickListener() {
            sportscouting.title = binding.sportscoutingTitle.text.toString()
            sportscouting.description = binding.description.text.toString()
            if (sportscouting.title.isNotEmpty()) {

                app.sportscoutings.add(sportscouting.copy())
                i("add Button Pressed: $sportscouting")
                for (i in app.sportscoutings.indices) {
                    i("SportScouting[$i]:${this.app.sportscoutings[i]}")
                }
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
}