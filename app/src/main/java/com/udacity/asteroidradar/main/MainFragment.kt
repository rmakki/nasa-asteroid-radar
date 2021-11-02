package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidRadarApplication
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    // Old code - initializing MainViewModel without the new parameter
    // this works too since MainViewModel extends AndroidViewModel
    //private val viewModel: MainViewModel by lazy {
    //    ViewModelProvider(this).get(MainViewModel::class.java)
    //}

    // This is not necessary, above code works. Only a must if I want a custom parameter
    // other than Application like repo...
    private val viewModel: MainViewModel by lazy {
        //ViewModelProvider(this).get(MainViewModel::class.java)
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        // use ViewModel factory function defined in ViewModel to get a reference to the
        // ViewModel with 'application' as parameter
        ViewModelProvider(
            this,
            MainViewModel.MainViewModelFactory(
                (activity.application as AsteroidRadarApplication).repository
            )
        ).get(MainViewModel::class.java)

        //ViewModelProvider(this,MainViewModel.Factory((activity.application))).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        binding = FragmentMainBinding.inflate(inflater, container, false )

        // scoped to the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        /**
        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.doneNavigatingToAsteroidDetails()
            }
        }) */

        viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.doneNavigatingToAsteroidDetails()
            }
        })

        // pass the asteroid object to the viewmodel
        val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener {
                //asteroid -> Toast.makeText(context, "${asteroid}", Toast.LENGTH_LONG).show()
                asteroid ->  viewModel.onAsteroidDetailsClicked(asteroid)

        })

        binding.asteroidRecycler.adapter = adapter

         //Moved to BindingAdapters
        // This is not necessary because we want to avoid the need to add
        // extra processing on the list in BindingAdapters. Works better for other simple views like Text
        /**
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

    // update asteroid display depending on selected option
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // filter LiveData triggers the update of the asteroidList LiveData
        viewModel.filter.value =
            when(item.itemId) {
                R.id.show_week_menu -> Constants.FILTER_WEEK
                R.id.show_today_menu -> Constants.FILTER_TODAY
                else -> Constants.FILTER_ALL
            }
        return true

    }
}
