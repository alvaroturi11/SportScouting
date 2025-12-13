package org.wit.sportscouting.main

import android.app.Application
import org.wit.sportscouting.models.SportScoutingModel
import timber.log.Timber
import timber.log.Timber.i
import org.wit.sportscouting.models.SportScoutingJSONStore

class MainApp : Application() {

    val sportscoutings = ArrayList<SportScoutingModel>()
    lateinit var store: SportScoutingJSONStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Sport Scouting started")

        store = SportScoutingJSONStore(this)
        sportscoutings.clear()
        sportscoutings.addAll(store.load())
    }

    fun persist() = store.saveAll(sportscoutings)
}