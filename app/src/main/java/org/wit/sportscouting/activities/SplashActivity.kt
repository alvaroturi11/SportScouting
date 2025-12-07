package org.wit.sportscouting.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import org.wit.sportscouting.R

class SplashActivity : AppCompatActivity() {

    // Splash duration in miliseconds (3s)
    private val splashDuration: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // After the delay, we open the main list and close the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            val next = Intent(this, SportScoutingListActivity::class.java)
            startActivity(next)
            finish() // So that the splash screen cannot be returned to.
        }, splashDuration)
    }
}
