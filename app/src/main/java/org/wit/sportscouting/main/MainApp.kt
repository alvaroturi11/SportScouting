package org.wit.sportscouting.main

import android.app.Application
import org.wit.sportscouting.models.SportScoutingModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val sportscoutings = ArrayList<SportScoutingModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Sport Scouting started")
//        sportscoutings.add(SportScoutingModel("One", "About one..."))
//        sportscoutings.add(SportScoutingModel("Two", "About two..."))
//        sportscoutings.add(SportScoutingModel("Three", "About three..."))
    }
}
/*import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.wit.sportscouting.R

class MainApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sportscouting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
*/
