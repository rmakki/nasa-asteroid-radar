package com.udacity.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Util
import com.udacity.asteroidradar.R


class AsteroidAdapter: RecyclerView.Adapter<Util.TextItemViewHolder>() {

     var data = listOf <Asteroid>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Util.TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.codename
        Log.i("Recyle item", item.codename)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Util.TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.text_item_view, parent, false) as TextView
        return Util.TextItemViewHolder(view)    }

}