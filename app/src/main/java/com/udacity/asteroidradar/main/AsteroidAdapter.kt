package com.udacity.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Util
import com.udacity.asteroidradar.R


class AsteroidAdapter: RecyclerView.Adapter<AsteroidAdapter.ViewHolder>() {

     var data = listOf <Asteroid>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.asteroid_id.text = item.codename
        Log.i("Recyle item", item.codename)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_asteroid, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val asteroid_id: TextView = itemView.findViewById(R.id.asteroid_id)
        val asteroid_date_approaching: TextView = itemView.findViewById(R.id.asteroid_date_approaching)
        val asteroid_status_image: ImageView = itemView.findViewById(R.id.status_image)
    }

}