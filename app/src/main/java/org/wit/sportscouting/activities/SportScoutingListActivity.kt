package org.wit.sportscouting.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
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
        adapter = SportScoutingAdapter(app.sportscoutings)
        binding.recyclerView.adapter = adapter

        // Trash button
        binding.btnHeaderDelete.setOnClickListener {
            showDeleteAllDialog()
        }

        // Add button
        binding.btnHeaderAdd.setOnClickListener {
            val launcherIntent = Intent(this, SportScoutingActivity::class.java)
            getResult.launch(launcherIntent)
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

        updateDashboard(app.sportscoutings)
    }

    override fun onResume() {
        super.onResume()
        val q = binding.searchView.query?.toString().orEmpty()
        applyFilter(q)
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_title))
            .setMessage(getString(R.string.dialog_delete_message))
            .setNegativeButton(getString(R.string.action_cancel), null)
            .setPositiveButton(getString(R.string.action_delete)) { _, _ ->
                app.sportscoutings.clear()
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
                // Mantén el estado en memoria
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
        val base = app.sportscoutings //list of new players I add

        //Search by name
        val filtered = if (q.isEmpty()){
            base
        }
        else{
            base.filter {
                it.title.lowercase().contains(q)
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
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.sportscoutings.size)
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
            binding.sportscoutingTitle.text = sportscouting.title
            //binding.description.text = sportscouting.description
            val team = sportscouting.description.trim()
            val pos = sportscouting.position.trim()
            binding.description.text =
                if (pos.isNotEmpty()) "$team • $pos" else team

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
                val intent = Intent(ctx, SportScoutingActivity::class.java)
                intent.putExtra("editIndex", bindingAdapterPosition)
                ctx.startActivity(intent)
            }
        }
    }
}