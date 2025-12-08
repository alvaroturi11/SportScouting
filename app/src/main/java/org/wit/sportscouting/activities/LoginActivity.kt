package org.wit.sportscouting.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.sportscouting.databinding.ActivityLoginBinding
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PASSWORD = "user_password"
        private const val KEY_LOGGED_IN = "logged_in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.btnSignup.setOnClickListener { handleSignup() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    // Load all users saved in SharedPreferences as a map email -> password
    private fun loadAllUsers(): MutableMap<String, String> {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val json = prefs.getString("all_users", "{}") ?: "{}"

        return try {
            val jsonObj = JSONObject(json)
            val map = mutableMapOf<String, String>()
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = jsonObj.getString(key)
            }
            map
        } catch (e: Exception) {
            mutableMapOf()
        }
    }

    // Save the user map in SharedPreferences as JSON
    private fun saveAllUsers(map: Map<String, String>) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val jsonObj = JSONObject(map)
        prefs.edit()
            .putString("all_users", jsonObj.toString())
            .apply()
    }

    private fun handleSignup() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Email and password are required")
            return
        }

        val users = loadAllUsers()

        // If a user already exists with this email address, we will not allow them to register again.
        if (users.containsKey(email)) {
            showMessage("User already exists. Please use Login.")
            return
        }

        // We added this new user to the map
        users[email] = password
        saveAllUsers(users)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()

        showMessage("User registered successfully")

        // Delay so the user can read the message
        Handler(Looper.getMainLooper()).postDelayed({
            goToList()
        }, 1500)
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Email and password are required")
            return
        }

        val users = loadAllUsers()
        val savedPassword = users[email]

        if (savedPassword == password) {
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply()

            showMessage("Logged in successfully")

            Handler(Looper.getMainLooper()).postDelayed({
                goToList()
            }, 1500)
        } else {
            showMessage("Invalid credentials")
        }
    }

    private fun showMessage(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    private fun goToList() {
        val intent = Intent(this, SportScoutingListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
