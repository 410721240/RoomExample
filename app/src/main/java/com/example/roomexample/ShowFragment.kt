package com.example.roomexample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.roomexample.databinding.FragmentShowBinding
import com.example.roomexample.firebase.asScene
import com.example.roomexample.utilities.showSnackMessage
import com.google.android.material.bottomnavigation.BottomNavigationView

//display the details of one selected message from the server
class ShowFragment : Fragment() {

    private lateinit var binding: FragmentShowBinding
    private lateinit var viewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show, container, false)

        //shared viewmodel with the activity
        viewModel = ViewModelProvider(requireActivity(),
            MyViewModelFactory(requireActivity().application)).get(MyViewModel::class.java)

        //display the selected remote message(scene)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //add the selected scene into the local database
        binding.saveButton.setOnClickListener {
            val newScene = viewModel.selectedMessage.value!!.asScene()
            viewModel.insertScene(newScene)
            showSnackMessage(requireView(), "Complete data saving")
        }

        //increase the support count
        binding.loveButton.setOnClickListener {
            viewModel.increaseSupport()
        }

        //hide bottom nav
        this.activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility = View.GONE

        return binding.root
    }

}