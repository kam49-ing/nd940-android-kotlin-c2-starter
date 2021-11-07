package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.AsteroidDatabase
import com.udacity.asteroidradar.AsteroidViewModelFactory
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        val dataSource = AsteroidDatabase.getInstance(this.requireContext()).asteroidDatabaseDao

        val asteroidViewModelFactory = AsteroidViewModelFactory(dataSource, application)

        val mainViewModel:MainViewModel = ViewModelProvider(this, asteroidViewModelFactory).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel

        val adapter = AsteroidAdapter(AsteroidListener { asteroidId ->
            mainViewModel.onAsteroidClicked(asteroidId)
        })


        binding.asteroidRecycler.adapter = adapter

        //list all asteroids from database from today onwards
        mainViewModel.asteroids.observe(this.requireActivity(), Observer { asteroids->
            asteroids?.let {
                adapter.submitList(asteroids)
            }
        })

        //show detail fragment onclick on an asteroid
        mainViewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer { asteroid->
            asteroid?.let {
                //navigate to detail fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                //tell the fragment that navigation was done
                mainViewModel.onAsteroidDetailNavigated()
            }
        })

        mainViewModel.getAsteroidProperties()


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
