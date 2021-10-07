package com.udacity.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.ListItemAsteroidBinding

class AsteroidAdapter: ListAdapter<Asteroid,
        AsteroidAdapter.ViewHolder>(AsteroidDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor (val binding: ListItemAsteroidBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Asteroid) {
            binding.asteroidId.text = item.codename
            binding.asteroidDateApproaching.text = item.closeApproachDate
            if (item.isPotentiallyHazardous) {
                binding.statusImage.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                binding.statusImage.setImageResource(R.drawable.ic_status_normal)

            }
            Log.i("Recycle item", item.codename)
        }

        companion object {
             fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAsteroidBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    /**
     * Callback for calculating the diff between two non-null items in a list.
     *
     * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
     * list that's been passed to `submitList`.
     */

    class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem

        }
    }
}