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

class SportScoutingListActivity : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivitySportscoutingListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportscoutingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = SportScoutingAdapter(app.sportscoutingss)
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

class SportScoutingAdapterAdapter(private var sportscoutings: List<SportScoutingModel>) :
    RecyclerView.Adapter<SportScoutingAdapterAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardSportscoutingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val sportscouting = sportscoutings[holder.adapterPosition]
        holder.bind(sportscouting)
    }

    override fun getItemCount(): Int = sportscoutings.size

    class MainHolder(private val binding : CardSportscoutingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sportscouting: SportScoutingModel) {
            binding.sportscoutingTitle.text = sportscouting.title
            binding.description.text = sportscouting.description
        }
    }
}