package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.list_item_asteroid.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding


    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //val binding: FragmentMainBinding = DataBindingUtil.inflate(
        //    inflater, R.layout.fragment_main, container, false)
        //binding.lifecycleOwner = this

        binding = FragmentMainBinding.inflate(inflater, container, false )
        // scoped to the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.doneNavigatingToAsteroidDetails()
            }
        })
        /**
        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, Observer {asteroid ->
            id?.let {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                //                 this.findNavController().navigate(MainFragmentDirections.actionShowDetail(viewModel.navigateToAsteroid.value))
                viewModel.doneNavigatingToAsteroidDetails()
            }
        })
        */

        // pass the asteroid object to the viewmodel
        val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener {
                //asteroid -> Toast.makeText(context, "${asteroid}", Toast.LENGTH_LONG).show()
                asteroid ->  viewModel.onAsteroidDetailsClicked(asteroid)

        })

        binding.asteroidRecycler.adapter = adapter

        /** Move to BindingAdapters ?
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer {

            it?.let {
                adapter.submitList(it)
            }
        })
        */

        setHasOptionsMenu(true)

        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
