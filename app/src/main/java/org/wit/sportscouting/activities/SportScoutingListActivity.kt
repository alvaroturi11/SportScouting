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

class SportScoutingListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivitySportscoutingListBinding
    private lateinit var adapter: SportScoutingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportscoutingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SportScoutingAdapter(app.sportscoutings)
        binding.recyclerView.adapter = adapter


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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyFilter(text: String) {
        val q = text.trim().lowercase()
        val base = app.sportscoutings //list of new players I add
        val filtered = if (q.isEmpty()){
            base
        }
        else{
            base.filter {
                it.title.lowercase().contains(q)
            }
        }
        adapter.updateData(filtered)
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