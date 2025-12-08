package org.wit.sportscouting.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.wit.sportscouting.databinding.ActivityPlayersMapBinding

class PlayersMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayersMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayersMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom bar - Map button
        binding.bottomNav.btnBottomMap.setOnClickListener {
            // no-op
        }

        // Bottom bar - Home button → volver a la lista principal
        binding.bottomNav.btnBottomHome.setOnClickListener {
            val intent = Intent(this, SportScoutingListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // (Opcional) botón more
        binding.bottomNav.btnBottomMore.setOnClickListener {
            // future feature
        }
    }
}
