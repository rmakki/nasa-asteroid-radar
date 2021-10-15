package com.udacity.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ListItemAsteroidBinding

class AsteroidAdapter(val clickListener: AsteroidListener): ListAdapter<Asteroid,
        AsteroidAdapter.ViewHolder>(AsteroidDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroid = getItem(position)
        // set OnClick listener of the RV view item
        holder.itemView.setOnClickListener {
            clickListener.onClick(asteroid)
        }
        holder.bind(asteroid)

        //holder.bind(clickListener,getItem(position)!!)

    }

    /* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemAsteroidBinding
            .inflate(LayoutInflater.from(parent.context)))
    } */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemAsteroidBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // ViewHolder for items to display Asteroid records
    class ViewHolder(private val binding: ListItemAsteroidBinding):
        RecyclerView.ViewHolder(binding.root) {

        // bind an entry from the data source to the view property
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.executePendingBindings()
            Log.i("Recycler item", asteroid.codename)

        }
    }
  /**
   * Old Implementation below if we are not using BindingAdapters.kt
   *
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          return ViewHolder.from(parent)
    }
    class ViewHolder private constructor (val binding: ListItemAsteroidBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: AsteroidListener, item: Asteroid) {

            binding.asteroid = item
            binding.clickListener = clickListener

            binding.asteroidId.text = item.codename
            binding.asteroidDateApproaching.text = item.closeApproachDate
            if (item.isPotentiallyHazardous) {
                binding.statusImage.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                binding.statusImage.setImageResource(R.drawable.ic_status_normal)

            }

            binding.executePendingBindings()

            Log.i("Recycler item", item.codename)
        }

        companion object {
             fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAsteroidBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
**/

    /**
     * Callback for calculating the diff between two non-null items in a list.
     *
     * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
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

    // The listener class receives a Asteroid object and passes it
    class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}