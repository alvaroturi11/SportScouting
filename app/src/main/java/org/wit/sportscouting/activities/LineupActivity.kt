package org.wit.sportscouting.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.wit.sportscouting.R
import org.wit.sportscouting.databinding.ActivityLineupBinding
import org.wit.sportscouting.main.MainApp
import org.wit.sportscouting.models.SportScoutingModel

class LineupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLineupBinding
    private lateinit var app: MainApp

    // List of available players (current user only)
    private var availablePlayers: List<SportScoutingModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLineupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        // Load user players
        loadPlayersForCurrentUser()
        // Prepare the alignment gaps
        setupPositionPickers()
        // Restore saved lineup
        restoreSavedLineup()
        // Message if there are no players
        updateHint()
        // Bottom bar
        setupBottomBar()
    }

    private fun loadPlayersForCurrentUser() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("logged_in", false)
        val email = if (loggedIn) prefs.getString("user_email", null) else null

        val all = app.sportscoutings
        availablePlayers = if (email != null) {
            all.filter { it.ownerEmail == email && it.lineupPosition.isBlank() }
        } else {
            all.filter { it.ownerEmail.isNullOrEmpty() && it.lineupPosition.isBlank() }
        }
    }

    private fun restoreSavedLineup() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("logged_in", false)
        val email = if (loggedIn) prefs.getString("user_email", null) else null

        val all = app.sportscoutings

        // Only restore current user line up
        val playersOfUser = if (email != null) {
            all.filter { it.ownerEmail == email }
        } else {
            all.filter { it.ownerEmail.isNullOrEmpty() }
        }

        playersOfUser.forEach { player ->
            if (player.lineupPosition.isNotBlank()) {

                val targetGap: TextView? = when (player.lineupPosition) {
                    "GK"  -> binding.posGK
                    "DF1" -> binding.posDF1
                    "DF2" -> binding.posDF2
                    "DF3" -> binding.posDF3
                    "DF4" -> binding.posDF4
                    "MF1" -> binding.posMF1
                    "MF2" -> binding.posMF2
                    "MF3" -> binding.posMF3
                    "FW1" -> binding.posFW1
                    "FW2" -> binding.posFW2
                    "FW3" -> binding.posFW3
                    else -> null
                }

                targetGap?.let { updateGapAppearance(it, player) }
            }
        }
    }

    private fun getGapCode(gap: TextView): String {
        return when (gap.id) {
            binding.posGK.id -> "GK"
            binding.posDF1.id -> "DF1"
            binding.posDF2.id -> "DF2"
            binding.posDF3.id -> "DF3"
            binding.posDF4.id -> "DF4"
            binding.posMF1.id -> "MF1"
            binding.posMF2.id -> "MF2"
            binding.posMF3.id -> "MF3"
            binding.posFW1.id -> "FW1"
            binding.posFW2.id -> "FW2"
            binding.posFW3.id -> "FW3"
            else -> ""
        }
    }

    private fun getDefaultLabelForGap(gapCode: String): String {
        return when {
            gapCode.startsWith("GK") -> "GK"
            gapCode.startsWith("DF") -> "DF"
            gapCode.startsWith("MF") -> "MF"
            gapCode.startsWith("FW") -> "FW"
            else -> ""
        }
    }

    // Fill in the gap with the image or with the name.
    private fun updateGapAppearance(gap: TextView, player: SportScoutingModel?) {
        val gapCode = getGapCode(gap)

        if (player == null) {
            // Empty space → default text and blue background
            gap.text = getDefaultLabelForGap(gapCode)
            gap.setBackgroundResource(R.drawable.lineup_gap_bg)
            return
        }

        if (player.image.isNotBlank()) {
            try {
                val uri = Uri.parse(player.image)
                val input = contentResolver.openInputStream(uri)
                val drawable: Drawable? = input?.use {
                    Drawable.createFromStream(it, uri.toString())
                }

                if (drawable != null) {
                    gap.background = drawable   // use the image as a background
                    gap.text = ""
                } else {
                    // fallback: image could not be loaded → name
                    gap.setBackgroundResource(R.drawable.lineup_gap_bg)
                    gap.text = player.player
                }
            } catch (e: Exception) {
                // if something goes wrong with the image → name
                gap.setBackgroundResource(R.drawable.lineup_gap_bg)
                gap.text = player.player
            }
        } else {
            // Player without image → blue pill + name
            gap.setBackgroundResource(R.drawable.lineup_gap_bg)
            gap.text = player.player
        }
    }

    private fun updateHint() {
        if (availablePlayers.isNotEmpty()) {
            // There are free players → show message
            binding.tvHint.text = getString(R.string.lineup_hint)
            binding.tvHint.visibility = View.VISIBLE
        } else {
            // No players available → hide message
            binding.tvHint.text = ""
            binding.tvHint.visibility = View.INVISIBLE
        }
    }

    private fun setupPositionPickers() {
        val gaps: List<TextView> = listOf(
            binding.posGK,
            binding.posDF1, binding.posDF2, binding.posDF3, binding.posDF4,
            binding.posMF1, binding.posMF2, binding.posMF3,
            binding.posFW1, binding.posFW2, binding.posFW3
        )

        gaps.forEach { gap ->
            gap.setOnClickListener { onPositionClicked(gap) }
        }
    }

    private fun onPositionClicked(gap: TextView) {
        // Update available players
        loadPlayersForCurrentUser()

        val gapCode = getGapCode(gap)

        // Player currently assigned to this gap (of the current user)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("logged_in", false)
        val email = if (loggedIn) prefs.getString("user_email", null) else null
        val all = app.sportscoutings
        val userPlayers: List<SportScoutingModel> = if (email != null) {
            all.filter { it.ownerEmail == email }
        } else {
            all.filter { it.ownerEmail.isNullOrEmpty() }
        }
        val currentPlayerInGap = userPlayers.find { it.lineupPosition == gapCode }

        // If there is no player in the gap -> choose one
        if (currentPlayerInGap == null) {
            // No available players
            if (availablePlayers.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.lineup_no_players_title))
                    .setMessage(getString(R.string.lineup_no_players_detail))
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                return
            }
            showPlayerPickerForGap(gap, gapCode, null)
            return
        }

        // If there is a player in the gap -> replace or remove
        val options = arrayOf(
            getString(R.string.lineup_change_player),
            getString(R.string.lineup_remove_player),
            getString(android.R.string.cancel)
        )

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.lineup_gap_options))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> { // Change player
                        if (availablePlayers.isEmpty()) {
                            AlertDialog.Builder(this)
                                .setTitle(getString(R.string.lineup_no_players_title))
                                .setMessage(getString(R.string.lineup_no_players_detail))
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                        } else {
                            showPlayerPickerForGap(gap, gapCode, currentPlayerInGap)
                        }
                    }
                    1 -> { // Remove player from the gap
                        currentPlayerInGap.lineupPosition = ""
                        currentPlayerInGap.inLineup = false
                        updateGapAppearance(gap, null)
                        app.persist()
                        loadPlayersForCurrentUser()
                        updateHint()
                    }
                    2 -> dialog.dismiss() // Cancel
                }
            }
            .show()
    }

    // Displays the dialogue for choosing a free player for a gap
    private fun showPlayerPickerForGap(
        gap: TextView,
        gapCode: String,
        previousPlayer: SportScoutingModel?
    ) {
        val players = availablePlayers
        val names = players.map { it.player }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.lineup_choose_player))
            .setItems(names) { _, which ->
                val chosenPlayer = players[which]

                // Release the player who was in that position (if there was one)
                if (previousPlayer != null) {
                    previousPlayer.lineupPosition = ""
                    previousPlayer.inLineup = false
                }

                // Assign a new player to this slot
                chosenPlayer.lineupPosition = gapCode
                chosenPlayer.inLineup = true

                // Update text of the gap
                updateGapAppearance(gap, chosenPlayer)

                // Save changes
                app.persist()

                // Update list of available items and message
                loadPlayersForCurrentUser()
                updateHint()
            }
            .show()
    }

    private fun setupBottomBar() {
        // Bottom bar - Map button
        binding.bottomNav.btnBottomMap.setOnClickListener {
            val intent = Intent(this, PlayersMapActivity::class.java)
            startActivity(intent)
        }

        // Bottom bar - Home button
        binding.bottomNav.btnBottomHome.setOnClickListener {
            val intent = Intent(this, SportScoutingListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // Bottom bar - Team button
        binding.bottomNav.btnBottomMore.setOnClickListener {
            // no-op
        }
    }
}
