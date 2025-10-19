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
