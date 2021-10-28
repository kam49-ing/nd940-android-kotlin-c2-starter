package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val application = requireNotNull(this.activity).application

        val dataSource = AsteroidDatabase.getInstance(this.requireContext()).asteroidDatabaseDao

        val asteroidViewModelFactory = AsteroidViewModelFactory(dataSource, application)

        var asteroidViewModel:AsteroidViewModel = ViewModelProvider(this, asteroidViewModelFactory).get(AsteroidViewModel::class.java)

        binding.asteroidViewModel = asteroidViewModel

        asteroidViewModel.asteroids.observe(this.requireActivity(), Observer { asteroids->
            for (asteroid in asteroids){
                Log.i("MainFragment", "asteroid id: $asteroid.id")
            }
        })
        
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
