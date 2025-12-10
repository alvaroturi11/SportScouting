package org.wit.sportscouting.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.sportscouting.R
import org.wit.sportscouting.databinding.ActivitySportscoutingListBinding
import org.wit.sportscouting.databinding.CardSportscoutingBinding
import org.wit.sportscouting.main.MainApp
import org.wit.sportscouting.models.SportScoutingModel
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import android.net.Uri


class SportScoutingListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivitySportscoutingListBinding
    private lateinit var adapter: SportScoutingAdapter

    private val selected = booleanArrayOf(false, false, false, false)
    private val positions = arrayOf("goalkeeper", "defender", "midfielder", "forward")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportscoutingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.toolbar.title = title
        //setSupportActionBar(binding.toolbar)

        app = application as MainApp

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SportScoutingAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        //adapter = SportScoutingAdapter(app.sportscoutings)
        //binding.recyclerView.adapter = adapter

        // Trash button
        binding.btnHeaderDelete.setOnClickListener {
            showDeleteAllDialog()
        }

        // Add button
        binding.btnHeaderAdd.setOnClickListener {
            val launcherIntent = Intent(this, SportScoutingActivity::class.java)
            getResult.launch(launcherIntent)
        }

        // Login button
        binding.btnHeaderLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_top,
                0
            )
        }

        // Profile button
        binding.btnHeaderProfile.setOnClickListener { view ->
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val email = prefs.getString("user_email", "Unknown user")

            val popup = PopupMenu(this, view)
            // Línea informativa con el email (deshabilitada)
            popup.menu.add("Logged as: $email").isEnabled = false
            // Opción de logout
            popup.menu.add("Logout")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Logout" -> {
                        logoutUser()   // ahora solo cierra sesión y actualiza botones
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }

        //Search by name
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                applyFilter(query.orEmpty())
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                applyFilter(newText.orEmpty())
                return true
            }
        })

        //Filter button
        binding.btnFilter.setOnClickListener { showFilterDialog() }

        // Bottom bar - Map button
        binding.bottomNav.btnBottomMap.setOnClickListener {
            startActivity(Intent(this, PlayersMapActivity::class.java))
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        // Bottom bar - Home button
        binding.bottomNav.btnBottomHome.setOnClickListener {
            binding.searchView.setQuery("", false)
            selected.fill(false)
            applyFilter("")
        }

        // Bottom bar - Lineup button
        binding.bottomNav.btnBottomMore.setOnClickListener {
            startActivity(Intent(this, LineupActivity::class.java))
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        // Initial list + buttons according to user
        applyFilter("")
        updateDashboard(app.sportscoutings)
        updateUserButtons()
    }

    override fun onResume() {
        super.onResume()
        val q = binding.searchView.query?.toString().orEmpty()
        applyFilter(q)
        updateUserButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, SportScoutingActivity::class.java)
                getResult.launch(launcherIntent)
            }
            R.id.item_delete_all -> {
                showDeleteAllDialog()
            }
            R.id.item_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.item_logout -> {
                logoutUser()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentUserEmail(): String? {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("logged_in", false)
        return if (loggedIn) {
            prefs.getString("user_email", null)
        } else {
            null
        }
    }

    private fun updateUserButtons() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("logged_in", false)

        if (loggedIn) {
            binding.btnHeaderLogin.visibility = View.GONE
            binding.btnHeaderProfile.visibility = View.VISIBLE
        } else {
            binding.btnHeaderLogin.visibility = View.VISIBLE
            binding.btnHeaderProfile.visibility = View.GONE
        }
    }

    private fun logoutUser() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("logged_in", false).apply()

        updateUserButtons()
        applyFilter(binding.searchView.query?.toString().orEmpty())
    }

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message))
            .setNegativeButton(getString(R.string.action_cancel), null)
            .setPositiveButton(getString(R.string.action_delete)) { _, _ ->

                /*app.sportscoutings.clear()
                app.persist()
                binding.searchView.setQuery("", false)
                applyFilter("")*/
                // Remove only current user's players
                val email = getCurrentUserEmail()
                if (email != null) {
                    app.sportscoutings.removeAll { it.ownerEmail == email }
                    app.persist()
                }

                binding.searchView.setQuery("", false)
                applyFilter("")
            }
            .show()
    }
    private fun showFilterDialog() {
        // Position tags
        val labels = arrayOf(
            getString(R.string.pos_goalkeeper),
            getString(R.string.pos_defender),
            getString(R.string.pos_midfielder),
            getString(R.string.pos_forward)
        )

        // Filters state
        val oldState = selected.copyOf()

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.filter_by_position))
            .setMultiChoiceItems(labels, selected) { _, j, isChecked ->
                // The info stays in the array
                selected[j] = isChecked
            }
            // Apply the filter
            .setPositiveButton(getString(R.string.filters_apply)) { dialog, _ ->
                applyFilter(binding.searchView.query?.toString().orEmpty())
                dialog.dismiss()
            }
            // Clear
            .setNeutralButton(getString(R.string.filters_clear), null)
            // Cancel
            .setNegativeButton(getString(R.string.filters_cancel)) { _, _ ->
                for (i in selected.indices) {
                    selected[i] = oldState[i]
                }
            }

        val dialog = builder.create()

        dialog.setOnShowListener {
            // "Clear" do not close the dialolg
            val clearBtn = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL)
            clearBtn.setOnClickListener {
                // 1) Clean state
                selected.fill(false)
                // 2) Uncheck the boxes of the filter dialog
                val lv = dialog.listView
                for (i in 0 until lv.count) lv.setItemChecked(i, false)
            }
        }

        dialog.show()
    }

    private fun applyFilter(text: String) {
        val q = text.trim().lowercase()
        //val base = app.sportscoutings //list of new players I add
        val currentEmail = getCurrentUserEmail()

        // base = only actual user's players
        val baseAll = app.sportscoutings
        
        val base = if (currentEmail != null) {
            // Logged-in user: only their players
            baseAll.filter { it.ownerEmail == currentEmail }
        } else {
            // No username: only anonymous players
            baseAll.filter { it.ownerEmail.isNullOrEmpty() }
        }

        //Search by name
        val filtered = if (q.isEmpty()){
            base
        }
        else{
            base.filter {
                it.player.lowercase().contains(q)
            }
        }

        //Filter by positions
        val selectedPositions = positions
            .mapIndexedNotNull { i, key -> if (selected[i]) key else null }
            .toSet()

        val finalFiltered =
            if (selectedPositions.isEmpty()) filtered
            else filtered.filter { it.position.lowercase() in selectedPositions }

        updateDashboard(finalFiltered)

        adapter.updateData(finalFiltered)
    }

    private fun updateDashboard(current: List<SportScoutingModel>) {
        val gk = current.count { it.position.equals("goalkeeper", ignoreCase = true) }
        val df = current.count { it.position.equals("defender", ignoreCase = true) }
        val mf = current.count { it.position.equals("midfielder", ignoreCase = true) }
        val fw = current.count { it.position.equals("forward", ignoreCase = true) }

        binding.tvCountTotal.text = current.size.toString()
        binding.tvCountGK.text = gk.toString()
        binding.tvCountDF.text = df.toString()
        binding.tvCountMF.text = mf.toString()
        binding.tvCountFW.text = fw.toString()
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                //(binding.recyclerView.adapter)?.
                //notifyItemRangeChanged(0,app.sportscoutings.size)
                val q = binding.searchView.query?.toString().orEmpty()
                applyFilter(q)
            }
        }
    }

class SportScoutingAdapter(private var sportscoutings: List<SportScoutingModel>) :
    RecyclerView.Adapter<SportScoutingAdapter.MainHolder>() {

    fun updateData(newItems: List<SportScoutingModel>) {
        sportscoutings = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardSportscoutingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val sportscouting = sportscoutings[holder.adapterPosition]
        holder.bind(sportscouting)
        //holder.bind(sportscoutings[position])
    }

    override fun getItemCount(): Int = sportscoutings.size


    class MainHolder(private val binding : CardSportscoutingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sportscouting: SportScoutingModel) {
            binding.player.text = sportscouting.player
            //binding.description.text = sportscouting.description
            val team = sportscouting.team.trim()
            val pos = sportscouting.position.trim()
            binding.team.text =
                if (pos.isNotEmpty()) "$team • $pos" else team

            // Player image
            if (sportscouting.image.isNotBlank()) {
                binding.ivPlayer.setImageURI(Uri.parse(sportscouting.image))
            } else {
                binding.ivPlayer.setImageResource(R.drawable.outline_account_circle_24)
            }

            // Player card color
            val ctx = binding.root.context
            val colorRes = when (pos.lowercase()) {
                "forward"    -> R.color.cardForward
                "midfielder" -> R.color.cardMidfielder
                "defender"   -> R.color.cardDefender
                "goalkeeper" -> R.color.cardGoalkeeper
                else         -> R.color.cardDefault
            }
            binding.cardRoot.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(ctx, colorRes)
            )

            //Edit a player
            binding.btnEdit.setOnClickListener {
                val ctx = it.context
                val app = ctx.applicationContext as MainApp
                val fullIndex = app.sportscoutings.indexOf(sportscouting) // index of the full list

                val intent = Intent(ctx, SportScoutingActivity::class.java)
                intent.putExtra("editIndex", fullIndex) // real index for editing
                ctx.startActivity(intent)
            }
        }
    }
}