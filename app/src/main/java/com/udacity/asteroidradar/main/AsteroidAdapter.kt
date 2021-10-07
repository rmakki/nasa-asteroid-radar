package com.udacity.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R

// class AsteroidAdapter: RecyclerView.Adapter<AsteroidAdapter.ViewHolder>() {
class AsteroidAdapter: ListAdapter<Asteroid,
        AsteroidAdapter.ViewHolder>(AsteroidDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor (itemView: View) : RecyclerView.ViewHolder(itemView){
        val asteroid_id: TextView = itemView.findViewById(R.id.asteroid_id)
        val asteroid_date_approaching: TextView = itemView.findViewById(R.id.asteroid_date_approaching)
        val asteroid_status_image: ImageView = itemView.findViewById(R.id.status_image)

        fun bind(item: Asteroid) {

            asteroid_id.text = item.codename
            asteroid_date_approaching.text = item.closeApproachDate
            if (item.isPotentiallyHazardous) {
                asteroid_status_image.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                asteroid_status_image.setImageResource(R.drawable.ic_status_normal)

            }
            Log.i("Recycle item", item.codename)
        }

        companion object {
             fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_asteroid, parent, false)

                return ViewHolder(view)
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