package com.rcb.tickets.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rcb.tickets.databinding.ItemEventBinding
import com.rcb.tickets.model.Event

class EventAdapter(private val events: MutableList<Event> = mutableListOf()) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.tvTeams.text = "${event.team1 ?: "RCB"} vs ${event.team2 ?: "TBD"}"
            binding.tvEventName.text = event.eventName ?: ""
            binding.tvDate.text = event.eventDisplayDate ?: event.eventDate ?: ""
            binding.tvVenue.text = buildString {
                append(event.venueName ?: "")
                if (!event.cityName.isNullOrEmpty()) append(", ${event.cityName}")
            }
            binding.tvPriceRange.text = event.eventPriceRange ?: ""

            val btnText = event.eventButtonText ?: ""
            if (btnText.isNotEmpty()) {
                binding.tvStatus.visibility = View.VISIBLE
                binding.tvStatus.text = btnText
                binding.tvStatus.setBackgroundColor(
                    if (btnText.equals("SOLD OUT", ignoreCase = true)) 0xFF555555.toInt()
                    else 0xFFCC0000.toInt()
                )
            } else {
                binding.tvStatus.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}
