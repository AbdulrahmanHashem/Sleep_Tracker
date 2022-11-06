/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sleeptracker.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.sleeptracker.R
import com.example.android.sleeptracker.database.SleepDatabase
import com.example.android.sleeptracker.database.SleepNight
import com.example.android.sleeptracker.databinding.FragmentSleepTrackerBinding
import com.example.android.sleeptracker.sleeptracker.sleeplist.SleepNightAdapter
import com.example.android.sleeptracker.sleeptracker.sleeplist.SleepNightListener

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */

    lateinit var binding : FragmentSleepTrackerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        binding = FragmentSleepTrackerBinding.inflate(inflater, container, false)

        val dataSource = SleepDatabase.getInstance(requireActivity().application).sleepDatabaseDao

        val sleepTrackerViewModelFactory = SleepTrackerViewModelFactory(dataSource, requireActivity().application)

        val sleepTrackerViewModel = ViewModelProvider(this, sleepTrackerViewModelFactory)
            .get(SleepTrackerViewModel::class.java)

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        val manager = GridLayoutManager(requireActivity(), 3)
        binding.sleepList.layoutManager = manager

        binding.setLifecycleOwner(this)

        val adapter = SleepNightAdapter(SleepNightListener { nighId ->
            sleepTrackerViewModel.onSleepNightClicked(nighId)
        })
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.navigateToSleepDetails.observe(this.viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(night))
                sleepTrackerViewModel.doneNavigatingToSleepDetails()
            }
        })

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        sleepTrackerViewModel.navigateToSleepQuality.observe(this.viewLifecycleOwner, Observer { sleepNight ->
            sleepNight?.let {
            this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(sleepNight.nightId))
            sleepTrackerViewModel.doneNavigating()
            }
        })

        return binding.root
    }
}
