package com.rcb.tickets

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rcb.tickets.adapter.EventAdapter
import com.rcb.tickets.api.RetrofitClient
import com.rcb.tickets.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EventAdapter

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            updateLoginState()
            fetchEvents()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        updateLoginState()
        fetchEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.swipeRefresh.setColorSchemeColors(0xFFCC0000.toInt())
        binding.swipeRefresh.setOnRefreshListener { fetchEvents() }

        binding.btnLogin.setOnClickListener {
            val token = LoginActivity.getToken(this)
            if (token != null) {
                LoginActivity.clearToken(this)
                updateLoginState()
                fetchEvents()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            } else {
                loginLauncher.launch(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun updateLoginState() {
        val token = LoginActivity.getToken(this)
        if (token != null) {
            binding.btnLogin.text = "Logout"
            binding.btnLogin.backgroundTintList =
                android.content.res.ColorStateList.valueOf(0xFF444444.toInt())
            binding.tvTokenStatus.text = "Logged in"
            binding.tvTokenStatus.setTextColor(0xFF00CC66.toInt())
        } else {
            binding.btnLogin.text = "Login"
            binding.btnLogin.backgroundTintList =
                android.content.res.ColorStateList.valueOf(0xFFCC0000.toInt())
            binding.tvTokenStatus.text = "Not logged in"
            binding.tvTokenStatus.setTextColor(0xFFAAAAAA.toInt())
        }
    }

    private fun fetchEvents() {
        val token = LoginActivity.getToken(this)
        val authHeader = token?.let { "Bearer $it" }

        setLoading(true)
        binding.tvStatus.text = "Loading events..."

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getEventList(authHeader)

                if (response.isSuccessful) {
                    val events = response.body()?.result
                    if (!events.isNullOrEmpty()) {
                        adapter.updateEvents(events)
                        binding.tvStatus.text = "${events.size} upcoming match${if (events.size > 1) "es" else ""}"
                    } else {
                        adapter.updateEvents(emptyList())
                        binding.tvStatus.text = "No upcoming matches"
                    }
                } else {
                    binding.tvStatus.text = "Failed to load  •  HTTP ${response.code()}"
                    if (response.code() == 401) {
                        LoginActivity.clearToken(this@MainActivity)
                        updateLoginState()
                        Toast.makeText(this@MainActivity, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                binding.tvStatus.text = "No internet connection"
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = false
    }
}
