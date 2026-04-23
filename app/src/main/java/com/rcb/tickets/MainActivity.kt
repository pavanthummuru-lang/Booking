package com.rcb.tickets

import android.os.Bundle
import android.view.View
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.swipeRefresh.setColorSchemeColors(0xFFCC0000.toInt())
        binding.swipeRefresh.setOnRefreshListener { fetchEvents() }

        fetchEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchEvents() {
        setLoading(true)
        binding.tvStatus.text = "Loading events..."

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getEventList()

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
                    Toast.makeText(this@MainActivity, "HTTP ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                binding.tvStatus.text = "No internet connection"
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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
