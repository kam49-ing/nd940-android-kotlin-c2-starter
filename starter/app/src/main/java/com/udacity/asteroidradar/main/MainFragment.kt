package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.AsteroidViewModelFactory
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.main.MainViewModel.*

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

        mainViewModel.asteroidWithFilter?.observe(this.viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it)
            }
        })

        mainViewModel.status.observe(this.viewLifecycleOwner, Observer {
            if(it == AsteroidStatus.ERROR) {
                Snackbar.make(
                    this.requireView(),
                    "Failed to connect to internet",
                    Snackbar.LENGTH_LONG
                ).show()
                mainViewModel.onSnackBarShowed()
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

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getAsteroid(
            when(item.itemId){
                //shows all saved asteroids
                R.id.show_buy_menu-> {
                    OptionMenu.SHOW_ALL
                }
                //showed asteroids of today
                R.id.show_rent_menu -> OptionMenu.SHOW_TODAY
                //shows asteroid for the week
                else->OptionMenu.SHOW_WEEK
            }
        )
        //showing message
        Snackbar.make(this.requireView(),
            when(item.itemId){
                R.id.show_buy_menu->"Showing all saved data"
                R.id.show_rent_menu -> "Showing today's Asteroid"
                else->"Showing week's Asteroid"
            } ,
            Snackbar.LENGTH_LONG)
            .show()
        return true
    }

}
